/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;

public class NotNullTraceReplacer extends FEReplacer {

	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();
	StmtExpr atomCall = null;
	int lastCallID = 0;
	// String state;
	HashMap<Expression, Integer> callerNotNullMap = new HashMap<Expression, Integer>();
	// HashMap<Expression, Statement> violation = new HashMap<Expression,
	// Statement>();

	public NotNullTraceReplacer(List<ASTLinePy> allLines, Function data) {
		this.allLines = allLines;
		String methodName = data.getName();
		if (methodName.contains("_"))
			methodName = methodName.substring(0, methodName.indexOf("_"));

		for (int i = allLines.size() - 1; i >= 0; i--) {
			// FIXME I know its buggy
			if (!allLines.get(i).getLinePyList().get(0).getMethodName().equals(methodName))
				continue;
			for (Statement stmt : allLines.get(i).getSkStmts()) {
				if (stmt instanceof StmtExpr) {
					Expression expr = ((StmtExpr) stmt).getExpression();
					if (expr instanceof ExprFunCall) {
						atomCall = (StmtExpr) stmt;
						lastCallID = i;
						// state = getState(i);
						return;
					}
				}
			}
		}

	}

	public Object visitStmtExpr(StmtExpr stmt) {
		if (atomCall == null)
			return super.visitStmtExpr(stmt);
		Expression expr = stmt.getExpression();
		List<Statement> list = new ArrayList<Statement>();
		list.add(stmt);
		if (expr instanceof ExprFunCall) {
			Expression invoker = ((ExprFunCall) expr).getParams().get(0);
			Expression exprBin = new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_NEQ, invoker, ExprNullPtr.nullPtr);
			callerNotNullMap.put(exprBin, 3);
			callerNotNullMap.put(new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_EQ, invoker, ExprNullPtr.nullPtr),
					-3);
			if (expr.toString().equals(atomCall.toString())) {
				// Expression invoker = ((ExprFunCall) expr).getParams().get(0);
				// Expression exprBin = new ExprBinary(stmt.getOrigin(),
				// ExprBinary.BINOP_NEQ, invoker,
				// ExprNullPtr.nullPtr);
				StmtAssert ass = new StmtAssert(stmt.getOrigin(), exprBin, false);
				list.add(ass);
			} else {
				// Expression invoker = ((ExprFunCall) expr).getParams().get(0);
				// ExprNew exprNew = new ExprNew(stmt.getOrigin(),
				// ExprBinary.BINOP_NEQ, invoker,
				// ExprNullPtr.nullPtr);

			}
		}
		return new StmtBlock(stmt.getOrigin(), list);
	}

	public Object visitStmtAssign(StmtAssign stmt) {
		List<ASTLinePy> lines = isTouched(stmt);
		if (lines.size() == 0 || !(stmt instanceof StmtAssign))
			return super.visitStmtAssign(stmt);

		Expression lhs = stmt.getLHS();
		List<Statement> list = new ArrayList<Statement>();
		list.add(stmt);

		StringBuilder builder = new StringBuilder();
		for (ASTLinePy line : lines)
			builder.append(line.getStateIfAny());
		String state = builder.toString();
		if (state.trim().length() == 0)
			return super.visitStmtAssign(stmt);
		if ((lhs instanceof ExprVar) && state.equals("null")) {
			StmtAssign assign = new StmtAssign(stmt.getOrigin(), lhs, ExprNullPtr.nullPtr);
			list.add(assign);
			Expression exprBin = new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_EQ, lhs, ExprNullPtr.nullPtr);
			callerNotNullMap.put(exprBin, 1);
		}
		return new StmtBlock(stmt.getOrigin(), list);
	}

	// private String getState(int i) {
	// for (; i >= 0; i--) {
	// String state = allLines.get(i).getState();
	// if (state.length() > 0)
	// return state;
	// }
	// return "";
	// }

	private List<ASTLinePy> isTouched(StmtAssign stmt) {
		List<ASTLinePy> candidates = new ArrayList<ASTLinePy>();
		for (ASTLinePy line : allLines) {
			for (Statement st : line.getSkStmts()) {
				if (st instanceof StmtAssign) {
					if (((StmtAssign) st).getLHS().toString().equals(stmt.getLHS().toString()))
						candidates.add(line);
				}
			}
		}
		return candidates;
	}

	public HashMap<Expression, Integer> getTraceInvariant() {
		return callerNotNullMap;
	}

	public HashMap<Expression, Integer> getViolation() {
		HashMap<Expression, Integer> violate = new HashMap<Expression, Integer>();
		HashMap<String, Expression> trace = new HashMap<String, Expression>();
		for (Expression exp : callerNotNullMap.keySet()) {
			String expS = exp.toString();
			if (trace.containsKey(expS)) {
				int v = callerNotNullMap.get(exp) * callerNotNullMap.get(trace.get(expS));
				if (v < 0)
					violate.put(exp, 100);
			} else
				trace.put(expS, exp);
		}
		return violate;
	}
}
