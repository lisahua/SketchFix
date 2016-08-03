/**
 * @author Lisa Aug 1, 2016 ExpressionAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

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

	private MethodDeclarationAdapter method;

	public ExpressionAdapter(MethodDeclarationAdapter method) {
		this.method = method;
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
			skExpr = new ExprNew(method.getMethodContext(), (Type) TypeAdapter.getInstance().transform(type), skParam,
					false);
			return skExpr;
		} else if (expr instanceof FieldAccess) {
			FieldAccess jField = (FieldAccess) expr;
			org.eclipse.jdt.core.dom.Expression exp = jField.getExpression();
			skExpr = (sketch.compiler.ast.core.exprs.Expression) transform(exp);
			ExprField field = new ExprField(method.getMethodContext(), skExpr, jField.getName().toString(), false);

			return field;
		} else if (expr instanceof ThisExpression) {
			return AbstractASTAdapter.getThisObj();
		} else if (expr instanceof MethodInvocation) {
			// MethodInvocation --> ExprFunCall
			MethodInvocation mtdInvoke = (MethodInvocation) expr;
			Expression invoker = (Expression) transform(mtdInvoke.getExpression());
			Type invokerType = null;
			if (invoker != null)
				invokerType = resolveType(invoker);
			List<org.eclipse.jdt.core.dom.Expression> arg = mtdInvoke.arguments();
			List<Type> typeArg = new ArrayList<Type>();
			List<Expression> expArg = new ArrayList<Expression>();
			if (invoker != null)
				expArg.add(invoker);
			for (org.eclipse.jdt.core.dom.Expression argExp : arg) {
				Expression exp = (Expression) transform(argExp);
				typeArg.add(resolveType(exp));
				expArg.add(exp);
			}
			StructDefGenerator.insertMethod(mtdInvoke.getName().toString(), invokerType, typeArg);
			ExprFunCall expCall = new ExprFunCall(method.getMethodContext(), mtdInvoke.getName().toString(), expArg);
			if (isJunitAsserts(expCall))
				return resolveJUnitAsserts(expCall);

			return expCall;
		} else if (expr instanceof InfixExpression) {
			// InfixExpression -->ExprBinary
			InfixExpression condExpr = (InfixExpression) expr;
			Expression left = (Expression) transform(condExpr.getLeftOperand());
			Expression right = (Expression) transform(condExpr.getRightOperand());
			ExprBinary exprBin = new ExprBinary(method.getMethodContext(), resolveOperator(condExpr.getOperator()),
					left, right);
			return exprBin;
		} else if (expr instanceof Name) {
			// VariableDeclarationExpression --> ExprVar
			Name varDecl = (Name) expr;
			return new ExprVar(method.getMethodContext(), varDecl.getFullyQualifiedName());
		} else if (expr instanceof Assignment) {
			Assignment assign = (Assignment) expr;
			Expression left = (Expression) transform(assign.getLeftHandSide());
			Expression right = (Expression) transform(assign.getRightHandSide());
			return new StmtAssign(method.getMethodContext(), left, right);
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
					return new ExprConstInt(method.getMethodContext(), number);
				} else {
					double number = Double.parseDouble(num);
					return new ExprConstFloat(method.getMethodContext(), number);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (expr instanceof BooleanLiteral) {
			BooleanLiteral bool = (BooleanLiteral) expr;
			boolean value = bool.booleanValue();
			if (value)
				return new ExprConstInt(method.getMethodContext(), 1);
			else
				new ExprConstInt(method.getMethodContext(), 0);
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

	public Type resolveType(Expression expr) {
		if (expr instanceof ExprNew) {
			return ((ExprNew) expr).getTypeToConstruct();
		} else if (expr instanceof ExprField) {
			ExprField fAccess = (ExprField) expr;
			Expression left = fAccess.getLeft();
			Type invoker = resolveType(left);
			return method.getFieldTypeOf(invoker.toString(), fAccess.getName());
		} else if (expr instanceof ExprFunCall) {
			ExprFunCall funCall = (ExprFunCall) expr;
			// TODO
		} else if (expr instanceof ExprBinary) {
			// TODO
		} else if (expr instanceof ExprVar) {
			return method.getVarType(((ExprVar) expr).getName());
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

	private Object resolveJUnitAsserts(ExprFunCall call) {
		String name = call.getName();
		List<Expression> param = call.getParams();
		if (name.equals("assertEquals")) {
			if (param.size() < 2)
				return null;
			return new ExprBinary(method.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0), param.get(1));
		} else if (name.equals("assertNull")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(method.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0), new ExprNullPtr());
		} else if (name.equals("assertNotNull")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(method.getMethodContext(), ExprBinary.BINOP_NEQ, param.get(0), new ExprNullPtr());
		} else if (name.equals("assertFalse")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(method.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0),
					new ExprConstInt(method.getMethodContext(), 0));
		} else if (name.equals("assertTrue")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(method.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0),
					new ExprConstInt(method.getMethodContext(), 1));
		}
		return null;
	}

	private boolean isJunitAsserts(ExprFunCall call) {
		// FIXME, maybe buggy?
		if (call.getName().contains("assert")) {
			return true;
		}
		return false;
	}
}
