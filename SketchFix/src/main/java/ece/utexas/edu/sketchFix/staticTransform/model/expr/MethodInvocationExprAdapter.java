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
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;

public class MethodInvocationExprAdapter extends ExpressionAdapter {

	public MethodInvocationExprAdapter(StatementAdapter method) {
		super(method);
	}

	@Override
	public Object transform(ASTNode node) {
		// TODO Auto-generated method stub
		MethodInvocation mtdInvoke = (MethodInvocation) node;
		Expression invoker = (Expression) transform(mtdInvoke.getExpression());
		String invokerType = null;
		MethodWrapper mtdModel=null;
		if (invoker != null) {
			invokerType = resolveType(invoker).toString();
			mtdModel = stmtAdapter.getMethodModel(invokerType, mtdInvoke.getName().toString());
		}
		List<org.eclipse.jdt.core.dom.Expression> arg = mtdInvoke.arguments();
		List<Expression> expArg = new ArrayList<Expression>();
		if (invoker != null)
			expArg.add(invoker);
		for (org.eclipse.jdt.core.dom.Expression argExp : arg) {
			Expression exp = (Expression) transform(argExp);
			expArg.add(exp);
		}
		if (mtdModel != null) {
			Type type = TypeAdapter.getType(mtdModel.getReturnType());
			if (type!=null)
			stmtAdapter.insertStmt(initNewObject(type));
		}

		ExprFunCall expCall = new ExprFunCall(stmtAdapter.getMethodContext(), mtdInvoke.getName().toString(), expArg);
		if (isJunitAsserts(expCall))
			return resolveJUnitAsserts(expCall);
		stmtAdapter.insertUseMethod(invokerType, mtdInvoke.getName().toString());
		return null;
	}

	private Expression resolveJUnitAsserts(ExprFunCall call) {
		String name = call.getName();
		List<Expression> param = call.getParams();
		if (name.equals("assertEquals")) {
			if (param.size() < 2)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0), param.get(param.size()-1));
		} else if (name.equals("assertNull")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_EQ, param.get(0), new ExprNullPtr());
		} else if (name.equals("assertNotNull")) {
			if (param.size() == 0)
				return null;
			return new ExprBinary(stmtAdapter.getMethodContext(), ExprBinary.BINOP_NEQ, param.get(0), new ExprNullPtr());
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
		List<ExprNamedParam> skParam = new ArrayList<ExprNamedParam>();
//		Type newType = TypeAdapter.getType(type);
		ExprNew newExpr = new ExprNew(stmtAdapter.getMethodContext(), newType, skParam, false);
		List<Type> types = new ArrayList<Type>();
		types.add(newType);
		List<String> names = new ArrayList<String>();
		names.add(getNextName());
		List<Expression> inits = new ArrayList<Expression>();
		inits.add(newExpr);
		StmtVarDecl stmt = new StmtVarDecl(stmtAdapter.getMethodContext(), types, names, inits);
		return stmt;
	}
	private boolean isJunitAsserts(ExprFunCall call) {
		// FIXME, maybe buggy?
		if (call.getName().contains("assert")) {
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
			Type type = resolveType(funCall.getParams().get(0));
			// TODO
		} else if (expr instanceof ExprBinary) {
			// TODO
		} else if (expr instanceof ExprVar) {
			return stmtAdapter.getVarType(((ExprVar) expr).getName());
		}
		return null;
	}
}
