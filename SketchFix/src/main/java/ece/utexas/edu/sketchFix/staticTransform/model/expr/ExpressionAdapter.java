/**
 * @author Lisa Aug 1, 2016 ExpressionAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.expr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StatementAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprConstFloat;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.ExprField;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprNamedParam;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.typs.Type;

public class ExpressionAdapter extends AbstractASTAdapter {

	protected StatementAdapter stmtAdapter;
	 protected MethodInvocationExprAdapter invokeAdapter;
	public ExpressionAdapter(StatementAdapter method) {
		this.stmtAdapter = method;
		invokeAdapter = new MethodInvocationExprAdapter(stmtAdapter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object transform(ASTNode node) {
		org.eclipse.jdt.core.dom.Expression expr = (org.eclipse.jdt.core.dom.Expression) node;
		sketch.compiler.ast.core.exprs.Expression skExpr = null;
		if (expr instanceof ClassInstanceCreation) {
			ClassInstanceCreation instNew = (ClassInstanceCreation) expr;
			org.eclipse.jdt.core.dom.Type type = instNew.getType();

			List<org.eclipse.jdt.core.dom.Expression> param = instNew.arguments();
			List<ExprNamedParam> skParam = new ArrayList<ExprNamedParam>();
			for (org.eclipse.jdt.core.dom.Expression e : param) {
				skParam.add((ExprNamedParam) transform(e));
			}
			skExpr = new ExprNew(stmtAdapter.getMethodContext(), (Type) TypeAdapter.getInstance().transform(type), skParam,
					false);
			return skExpr;
		} else if (expr instanceof FieldAccess) {
			FieldAccess jField = (FieldAccess) expr;
			org.eclipse.jdt.core.dom.Expression exp = jField.getExpression();
			skExpr = (sketch.compiler.ast.core.exprs.Expression) transform(exp);
			ExprField field = new ExprField(stmtAdapter.getMethodContext(), skExpr, jField.getName().toString(), false);

			return field;
		} else if (expr instanceof ThisExpression) {
			return AbstractASTAdapter.getThisObj();
		} else if (expr instanceof MethodInvocation) {
			// MethodInvocation --> ExprFunCall
			return invokeAdapter.transform(expr);
		} else if (expr instanceof InfixExpression) {
			// InfixExpression -->ExprBinary
			InfixExpression condExpr = (InfixExpression) expr;
			Expression left = (Expression) transform(condExpr.getLeftOperand());
			Expression right = (Expression) transform(condExpr.getRightOperand());
			ExprBinary exprBin = new ExprBinary(stmtAdapter.getMethodContext(), resolveOperator(condExpr.getOperator()),
					left, right);
			return exprBin;
		} else if (expr instanceof Name) {
			// VariableDeclarationExpression --> ExprVar
			Name varDecl = (Name) expr;
			return new ExprVar(stmtAdapter.getMethodContext(), varDecl.getFullyQualifiedName());
		} else if (expr instanceof Assignment) {
			Assignment assign = (Assignment) expr;
			Expression left = (Expression) transform(assign.getLeftHandSide());
			Expression right = (Expression) transform(assign.getRightHandSide());
			return new StmtAssign(stmtAdapter.getMethodContext(), left, right);
		} else if (expr instanceof VariableDeclarationExpression) {
			// TODO no idea
		} else if (expr instanceof NullLiteral) {
			return new ExprNullPtr();
		} else if (expr instanceof NumberLiteral) {
			String num = ((NumberLiteral) expr).getToken();
			try {
				if (!num.contains(".")) {
					// FIXME I know it's bug
					int number = Integer.parseInt(num);
					return new ExprConstInt(stmtAdapter.getMethodContext(), number);
				} else {
					double number = Double.parseDouble(num);
					return new ExprConstFloat(stmtAdapter.getMethodContext(), number);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (expr instanceof BooleanLiteral) {
			BooleanLiteral bool = (BooleanLiteral) expr;
			boolean value = bool.booleanValue();
			if (value)
				return new ExprConstInt(stmtAdapter.getMethodContext(), 1);
			else
				new ExprConstInt(stmtAdapter.getMethodContext(), 0);
		} else if (expr instanceof ParenthesizedExpression) {
			ParenthesizedExpression paren = (ParenthesizedExpression) expr;
			return transform(paren.getExpression());
		} else if (expr instanceof CastExpression) {
			CastExpression castExp = (CastExpression) expr;
			// TODO no idea
		} else if (expr instanceof ArrayAccess) {
			// TODO not fully support by sketch

		}
		return null;
	}



	private int resolveOperator(InfixExpression.Operator op) {
		if (op == InfixExpression.Operator.AND)
			return ExprBinary.BINOP_ADD;
		else if (op == InfixExpression.Operator.EQUALS)
			return ExprBinary.BINOP_EQ;
		else if (op == InfixExpression.Operator.LESS)
			return ExprBinary.BINOP_LT;
		else if (op == InfixExpression.Operator.GREATER)
			return ExprBinary.BINOP_GT;
		else if (op == InfixExpression.Operator.LESS_EQUALS)
			return ExprBinary.BINOP_LE;
		else if (op == InfixExpression.Operator.GREATER_EQUALS)
			return ExprBinary.BINOP_GE;
		else if (op == InfixExpression.Operator.NOT_EQUALS)
			return ExprBinary.BINOP_NEQ;
		else if (op == InfixExpression.Operator.OR)
			return ExprBinary.BINOP_OR;
		else if (op == InfixExpression.Operator.PLUS)
			return ExprBinary.BINOP_ADD;
		else if (op == InfixExpression.Operator.MINUS)
			return ExprBinary.BINOP_SUB;
		// TODO
		return 0;
	}

	

}
