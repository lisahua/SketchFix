/**
 * @author Lisa Aug 19, 2016 NullExceptionHandler.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;

import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;

public class NullExceptionHandler extends FEReplacer {
private String call = "";
	public NullExceptionHandler(String call) {
		//FIXME hacky
		this.call = call.trim();
	}
	
	public Object visitStmtExpr(StmtExpr stmt) {
		Expression expr = stmt.getExpression();
		List<Statement> list = new ArrayList<Statement>();
		list.add(stmt);
		if (expr instanceof ExprFunCall) {
			if (expr.toString().equals(call)) {
				Expression invoker = ((ExprFunCall) expr).getParams().get(0);
				Expression exprBin = new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_NEQ, invoker,
						ExprNullPtr.nullPtr);
				StmtAssert ass = new StmtAssert(stmt.getOrigin(), exprBin, false);
				list.add(ass);
			}
		}
		return new StmtBlock(stmt.getOrigin(), list);
	}
}
