/**
 * @author Lisa Aug 4, 2016 MethodInvocationExprAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.expr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StatementAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import sketch.compiler.ast.core.exprs.ExprBinary;
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
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;

@Deprecated
public class MethodInvocationExprAdapter extends ExpressionAdapter {

	public MethodInvocationExprAdapter(StatementAdapter method) {
		super(method);
	}

	@Override
	public Object transform(ASTNode node) {
		// TODO Auto-generated method stub
		MethodInvocation mtdInvoke = (MethodInvocation) node;
		String invokeMethod = mtdInvoke.getName().toString();
		if (isJunitAsserts(invokeMethod)) {
			return transformJUnit(mtdInvoke);
		}
		ExprFunCall funCall = getExprFunCall(mtdInvoke);
		StmtExpr newCall = new StmtExpr(stmtAdapter.getMethodContext(), funCall);
		stmtAdapter.insertStmt(newCall);
		return new ExprVar(stmtAdapter.getMethodContext(), stmtAdapter.getLastInsertVarName());
	}

	private Object transformJUnit(MethodInvocation mtdInvoke) {
		List<org.eclipse.jdt.core.dom.Expression> arg = mtdInvoke.arguments();
		List<Expression> expArg = new ArrayList<Expression>();

		for (org.eclipse.jdt.core.dom.Expression argExp : arg) {
			Expression exp = (Expression) super.transform(argExp);
			if (exp != null)
				expArg.add(exp);
			else {
				expArg.add(new ExprVar(stmtAdapter.getMethodContext(), stmtAdapter.getLastInsertVarName()));
			}
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
		Expression invoker = (Expression) super.transform(mtdInvoke.getExpression());
		String invokerType = null;
		if (invoker != null) {
			invokerType = resolveType(invoker).toString();
			mtdModel = stmtAdapter.getMethodModel(invokerType, mtdInvoke.getName().toString());
			stmtAdapter.insertUseMethod(invokerType, mtdInvoke.getName().toString());
		}
		List<org.eclipse.jdt.core.dom.Expression> arg = mtdInvoke.arguments();
		if (invoker != null)
			expArg.add(invoker);

		for (org.eclipse.jdt.core.dom.Expression argExp : arg) {
			Expression exp = (Expression) super.transform(argExp);
			expArg.add(exp);
		}
		if (mtdModel != null) {
			Type type = TypeAdapter.getType(mtdModel.getReturnType());
			if (type != null) {
				stmtAdapter.insertStmt(initNewObject(type));
				expArg.add(new ExprVar(stmtAdapter.getMethodContext(), stmtAdapter.getLastInsertVarName()));
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

	private Type resolveType(Expression expr) {
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
		}
		return null;
	}
}
