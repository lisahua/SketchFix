/**
 * @author Lisa Aug 1, 2016 ExpressionAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StatementAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import sketch.compiler.ast.core.exprs.ExprArrayInit;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprConstChar;
import sketch.compiler.ast.core.exprs.ExprConstFloat;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.ExprField;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprNamedParam;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypeArray;
import sketch.compiler.ast.core.typs.TypePrimitive;

public class ExpressionAdapter extends AbstractASTAdapter {

	protected StatementAdapter stmtAdapter;
	private HashMap<String, Type> arrayTypes = new HashMap<String, Type>();
	private Type currVarType = null;

	public ExpressionAdapter(StatementAdapter method) {
		this.stmtAdapter = method;
	}

	@Override
	@SuppressWarnings({ "unchecked", "unused" })
	public Object transform(ASTNode node) {
		org.eclipse.jdt.core.dom.Expression expr = (org.eclipse.jdt.core.dom.Expression) node;
		sketch.compiler.ast.core.exprs.Expression skExpr = null;
		if (expr instanceof ClassInstanceCreation) {
			ClassInstanceCreation instNew = (ClassInstanceCreation) expr;
			org.eclipse.jdt.core.dom.Type type = instNew.getType();

			List<org.eclipse.jdt.core.dom.Expression> param = instNew.arguments();
			List<ExprNamedParam> skParam = new ArrayList<ExprNamedParam>();
			for (org.eclipse.jdt.core.dom.Expression e : param) {
				Expression para = (Expression) transform(e);
				skParam.add((convExprParam(para)));
			}
			skExpr = new ExprNew(stmtAdapter.getMethodContext(), (Type) TypeAdapter.getType(type.toString()), skParam,
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
			MethodInvocation mtdInvoke = (MethodInvocation) node;
			String invokeMethod = mtdInvoke.getName().toString();
			if (isJunitAsserts(invokeMethod)) {
				return transformJUnit(mtdInvoke);
			}
			ExprFunCall funCall = getExprFunCall(mtdInvoke);
			StmtExpr newCall = new StmtExpr(stmtAdapter.getMethodContext(), funCall);
			stmtAdapter.insertStmt(newCall);
			return new ExprVar(stmtAdapter.getMethodContext(), stmtAdapter.getLastInsertVarName());
		} else if (expr instanceof InfixExpression) {
			// InfixExpression -->ExprBinary
			InfixExpression condExpr = (InfixExpression) expr;
			Expression left = (Expression) transform(condExpr.getLeftOperand());
			Expression right = (Expression) transform(condExpr.getRightOperand());
			ExprBinary exprBin = new ExprBinary(stmtAdapter.getMethodContext(), resolveOperator(condExpr.getOperator()),
					left, right);
			return exprBin;
		} else if (expr instanceof PostfixExpression) {
			// postfixExpression -->ExprBinary
			PostfixExpression condExpr = (PostfixExpression) expr;
			Expression left = (Expression) transform(condExpr.getOperand());
			ExprBinary exprBin = null;
			if (condExpr.getOperator() == PostfixExpression.Operator.DECREMENT)
				exprBin = new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_SUB, left, ExprConstInt.one);
			else
				exprBin = new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_ADD, left, ExprConstInt.one);
			StmtAssign assign = new StmtAssign(stmtAdapter.getMethodContext(), left, exprBin);
			stmtAdapter.insertStmt(assign);
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
			VariableDeclarationExpression varDecl = (VariableDeclarationExpression) expr;
			Type sType = TypeAdapter.getType(varDecl.getType().toString());
			List<VariableDeclarationFragment> frags = varDecl.fragments();
			if (frags.size() > 0) {
				VariableDeclarationFragment frag = frags.get(0);
				StmtVarDecl var = new StmtVarDecl(stmtAdapter.getMethodContext(), sType, frag.getName().toString(),
						(Expression) transform(frag.getInitializer()));
				return var;
			}
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

		} else if (expr instanceof CharacterLiteral) {
			CharacterLiteral charOrString = (CharacterLiteral) expr;
			// TODO char expression
		} else if (expr instanceof StringLiteral) {
			String strName = getNextName();

			StringLiteral string = (StringLiteral) expr;
			String value = string.getLiteralValue();
			ExprConstInt len = new ExprConstInt(stmtAdapter.getMethodContext(), value.length());
			TypeArray array = new TypeArray(TypePrimitive.chartype, len);
			stmtAdapter.insertVarDecl(strName, array);
			TypeAdapter.insertArrayType(array.toString(), array);
			List<Expression> initEle = new ArrayList<Expression>();
			for (char c : value.toCharArray())
				initEle.add(ExprConstChar.create(c));
			ExprArrayInit arrInit = new ExprArrayInit(stmtAdapter.getMethodContext(), initEle);
			StmtVarDecl varStmt = new StmtVarDecl(stmtAdapter.getMethodContext(), array, strName, arrInit);
			stmtAdapter.insertStmt(varStmt);
			return new ExprVar(stmtAdapter.getMethodContext(), strName);
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

	private Object transformJUnit(MethodInvocation mtdInvoke) {
		List<org.eclipse.jdt.core.dom.Expression> arg = mtdInvoke.arguments();
		List<Expression> expArg = new ArrayList<Expression>();

		for (int i = 0; i < arg.size(); i++) {
			Expression exp = (Expression) transform(arg.get(i));
			currVarType = resolveType(exp);
			if (exp != null)
				expArg.add(exp);
			else
				expArg.add(new ExprVar(stmtAdapter.getMethodContext(), stmtAdapter.getLastInsertVarName()));
		}

		ExprFunCall expCall = new ExprFunCall(stmtAdapter.getMethodContext(), mtdInvoke.getName().toString(), expArg);
		Expression exp = resolveJUnitAsserts(expCall);
		StmtAssert newAss = new StmtAssert(stmtAdapter.getMethodContext(), exp, false);
		stmtAdapter.insertStmt(newAss);

		return null;
	}

	private ExprFunCall getExprFunCall(MethodInvocation mtdInvoke) {
		MethodWrapper mtdModel = null;
		List<Expression> expArg = new ArrayList<Expression>();
		Expression invoker = (Expression) transform(mtdInvoke.getExpression());
		String invokerType = null;
		// FIXME known bug: if invoker==null, should add this obj
		if (invoker == null)
			invoker = thisObj;
		invokerType = resolveType(invoker).toString();
		mtdModel = stmtAdapter.getMethodModel(invokerType, mtdInvoke.getName().toString());
		stmtAdapter.insertUseMethod(invokerType, mtdInvoke.getName().toString());

		List<org.eclipse.jdt.core.dom.Expression> arg = mtdInvoke.arguments();
		expArg.add(invoker);

		for (int i = 0; i < arg.size(); i++) {
			Expression exp = (Expression) transform(arg.get(i));
			if (exp != null) {
				expArg.add(exp);
				if (mtdModel != null && resolveType(exp) != null)
					stmtAdapter.updateParaType(invokerType, mtdModel.getMethodName(), i, resolveType(exp).toString());
			}
		}
		if (mtdModel != null) {
			Type type = TypeAdapter.getType(mtdModel.getReturnType());
			if (mtdModel.getReturnType() == null && type == null)
				type = currVarType;
			if (type != null) {
				stmtAdapter.insertStmt(initNewObject(type));
				expArg.add(new ExprVar(stmtAdapter.getMethodContext(), stmtAdapter.getLastInsertVarName()));
				stmtAdapter.updateParaType(invokerType, mtdModel.getMethodName(), 10, type.toString());
			}
		}

		ExprFunCall expCall = new ExprFunCall(stmtAdapter.getMethodContext(), mtdInvoke.getName().toString(), expArg);
		return expCall;
	}

	private Expression resolveJUnitAsserts(ExprFunCall call) {
		String name = call.getName();
		List<Expression> param = call.getParams();
		if (name.equals("assertEquals")) {
			if (param.size() < 2)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0),
					param.get(param.size() - 1));
		} else if (name.equals("assertNull")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0), new ExprNullPtr());
		} else if (name.equals("assertNotNull")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_NEQ, param.get(0),
					new ExprNullPtr());
		} else if (name.equals("assertFalse")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0),
					new ExprConstInt(stmtAdapter.getMethodContext(), 0));
		} else if (name.equals("assertTrue")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0),
					new ExprConstInt(stmtAdapter.getMethodContext(), 1));
		}
		return null;
	}

	private Statement initNewObject(Type newType) {
		StmtVarDecl stmt = null;
		if (newType instanceof TypePrimitive) {
			TypePrimitive type = (TypePrimitive) newType;
			stmt = new StmtVarDecl(stmtAdapter.getMethodContext(), type, getNextName(), type.defaultValue());
		} else {
			List<ExprNamedParam> skParam = new ArrayList<ExprNamedParam>();
			// Type newType = TypeAdapter.getType(type);
			ExprNew newExpr = new ExprNew(stmtAdapter.getMethodContext(), newType, skParam, false);
			stmt = new StmtVarDecl(stmtAdapter.getMethodContext(), newType, getNextName(), newExpr);
		}
		stmtAdapter.insertVarDecl(stmt.getName(0), newType);
		return stmt;
	}

	private boolean isJunitAsserts(String name) {
		// FIXME, maybe buggy?
		if (name.contains("assert")) {
			return true;
		}
		return false;
	}

	public Type resolveType(Expression expr) {
		if (expr instanceof ExprNew) {
			return ((ExprNew) expr).getTypeToConstruct();
		} else if (expr instanceof ExprField) {
			ExprField fAccess = (ExprField) expr;
			Expression left = fAccess.getLeft();
			Type invoker = resolveType(left);
			return stmtAdapter.getFieldTypeOf(invoker.toString(), fAccess.getName());
		} else if (expr instanceof ExprFunCall) {
			ExprFunCall funCall = (ExprFunCall) expr;
			Expression rtnExp = funCall.getParams().get(funCall.getParams().size() - 1);
			Type type = resolveType(rtnExp);
			return type;
		} else if (expr instanceof ExprBinary) {
			// TODO
		} else if (expr instanceof ExprVar) {
			return stmtAdapter.getVarType(((ExprVar) expr).getName());
		} else if (expr instanceof ExprArrayInit) {
			return arrayTypes.get(expr.toString());

		} else if (expr instanceof ExprConstFloat) {
			return TypePrimitive.floattype;
		} else if (expr instanceof ExprConstInt) {
			return TypePrimitive.int32type;
		} else if (expr instanceof ExprConstChar) {
			return TypePrimitive.chartype;
		}
		return null;
	}

	public void setCurrVarType(Type currVarType) {
		this.currVarType = currVarType;
	}

	private ExprNamedParam convExprParam(Expression exp) {
		ExprNamedParam param = null;
		if (exp instanceof ExprVar) {
			ExprVar var = (ExprVar) exp;
			param = new ExprNamedParam(stmtAdapter.getMethodContext(), var.getName(), exp);
		} else if (exp instanceof ExprConstInt) {
			StmtVarDecl varDecl = new StmtVarDecl(stmtAdapter.getMethodContext(), TypePrimitive.int32type,
					getNextName(), exp);
			stmtAdapter.insertStmt(varDecl);
			param = new ExprNamedParam(stmtAdapter.getMethodContext(), varDecl.getName(0), exp);
		} else if (exp instanceof ExprConstChar) {
			StmtVarDecl varDecl = new StmtVarDecl(stmtAdapter.getMethodContext(), TypePrimitive.chartype,
					getNextName(), exp);
			stmtAdapter.insertStmt(varDecl);
			param = new ExprNamedParam(stmtAdapter.getMethodContext(), varDecl.getName(0), exp);
		} else if (exp instanceof ExprConstFloat) {
			StmtVarDecl varDecl = new StmtVarDecl(stmtAdapter.getMethodContext(), TypePrimitive.floattype,
					getNextName(), exp);
			stmtAdapter.insertStmt(varDecl);
			param = new ExprNamedParam(stmtAdapter.getMethodContext(), varDecl.getName(0), exp);
		} else if (exp instanceof ExprField) {
//FIXME dont know
		}
return param;
	}

}
