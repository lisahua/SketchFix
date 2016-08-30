/**
 * @author Lisa Aug 19, 2016 NullExceptionHandler.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ece.utexas.edu.sketchFix.repair.processor.CandidateTemplate;
import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;
import ece.utexas.edu.sketchFix.repair.processor.SkLineType;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.stmts.StmtVarDecl;

public class NullExceptionHandler extends CandidateTemplate {
	// ExprFunCall lastCall = null;
	Parameter returnObj = null;
	Stack<Statement> touchStmt;

	List<List<Statement>> allTouchStmts;
	// private int lastCallID = 0;
	Statement currStmt;
	// List<Statement> currSkStmt = null;
	// int touchListID = 0;
	// SkLineType skLineType;

	public NullExceptionHandler(SkCandidate generator) {
		super(generator);
		// candGenerator = superClass.candGenerator;
		if (scope == null || scope.size() == 0)
			return;
		touchStmt = new Stack<Statement>();
		allTouchStmts = generator.getAllCurrentFirstTouchStatement();

		for (List<Statement> list : allTouchStmts) {
			touchStmt.push(list.get(0));
		}
	}

	// public Object visitStmtExpr(StmtExpr stmt) {
	// Expression expr = stmt.getExpression();
	// if (skLineType != SkLineType.STExpr)
	// return super.visitStmtExpr(stmt);
	// if (currStmt == null)
	// return super.visitStmtExpr(stmt);
	// Expression currExp = ((StmtExpr) currStmt).getExpression();
	// if (!expr.toString().equals(currExp.toString()))
	// return super.visitStmtExpr(stmt);
	// if (!(expr instanceof ExprFunCall))
	// return super.visitStmtExpr(stmt);
	// // FIXME
	// if (currSkStmt.contains(stmt) && currStmt.equals(stmt))
	// return super.visitStmtExpr(stmt);
	// Expression invoker = ((ExprFunCall) expr).getParams().get(0);
	// Expression exprBin = new ExprBinary(invoker.getOrigin(),
	// ExprBinary.BINOP_EQ, invoker, ExprNullPtr.nullPtr);
	//
	// Statement update = insertBlock(exprBin, stmt);
	// originCand.updateSkLineHole(stmt, update, SkLineType.STBLOCK);
	// // initCurrStmt();
	// return update;
	//
	// }
	//
	// public Object visitStmtAssign(StmtAssign stmt) {
	// if (currStmt == null || !(currStmt instanceof StmtAssign))
	// return super.visitStmtAssign(stmt);
	// Expression rhs = ((StmtAssign) currStmt).getRHS();
	// if (!rhs.toString().equals(stmt.getRHS().toString()))
	// return super.visitStmtAssign(stmt);
	// if (currSkStmt.contains(stmt) && currStmt.equals(stmt))
	// return super.visitStmtAssign(stmt);
	// // FIXME buggy
	// Expression exprBin = null;
	// for (Statement nextStmt : currSkStmt) {
	// if (nextStmt instanceof StmtIfThen) {
	// exprBin = ((StmtIfThen) nextStmt).getCond();
	// break;
	// } else if (nextStmt instanceof StmtExpr) {
	// if (((StmtExpr) nextStmt).getExpression() instanceof ExprFunCall) {
	// Expression expr = ((ExprFunCall) ((StmtExpr)
	// nextStmt).getExpression()).getParams().get(0);
	// exprBin = new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_EQ, expr,
	// ExprNullPtr.nullPtr);
	// break;
	// }
	// }
	// }
	// if (exprBin == null)
	// exprBin = new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_EQ,
	// stmt.getRHS(), ExprNullPtr.nullPtr);
	// Statement update = insertBlock(exprBin, stmt);
	// originCand.updateSkLineHole(stmt, update, SkLineType.STBLOCK);
	// // initCurrStmt();
	// return update;
	// }
	//
	// public Object visitStmtVarDecl(StmtVarDecl stmt) {
	// if (currStmt == null || !(currStmt instanceof StmtVarDecl))
	// return super.visitStmtVarDecl(stmt);
	// StmtVarDecl curr = (StmtVarDecl) currStmt;
	// if (!curr.toString().equals(stmt.toString()))
	// return super.visitStmtVarDecl(stmt);
	// // Type type = curr.getType(0);
	// // if (!type.toString().equals(stmt.getType(0).toString()))
	// // return super.visitStmtVarDecl(stmt);
	// // if ((curr.getInit(0) == null && stmt.getInit(0) != null)
	// // || (curr.getInit(0) != null && stmt.getInit(0) == null))
	// // return super.visitStmtVarDecl(stmt);
	//
	// Expression exprBin = null;
	// for (Statement nextStmt : currSkStmt) {
	// if (nextStmt instanceof StmtExpr) {
	// if (((StmtExpr) nextStmt).getExpression() instanceof ExprFunCall) {
	// Expression expr = ((ExprFunCall) ((StmtExpr)
	// nextStmt).getExpression()).getParams().get(0);
	// exprBin = new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_EQ, expr,
	// ExprNullPtr.nullPtr);
	// break;
	// }
	// }
	// }
	// if (exprBin == null) {
	// for (Statement nextStmt : currSkStmt) {
	// if (nextStmt instanceof StmtIfThen) {
	// exprBin = ((StmtIfThen) nextStmt).getCond();
	// break;
	// }
	// }
	// }
	// if (exprBin == null)
	// return super.visitStmtVarDecl(stmt);
	// Statement update = insertBlock(exprBin, stmt);
	// originCand.updateSkLineHole(stmt, update, SkLineType.STBLOCK);
	// // initCurrStmt();
	// return update;
	//
	// // Expression rhs = ((StmtAssign) currStmt).getRHS();
	// // if (!rhs.toString().equals(stmt.getRHS().toString()))
	// // return super.visitStmtAssign(stmt);
	// //
	// // Statement update = insertBlock(stmt.getLHS(), stmt);
	// // originCand.updateSkLineHole(stmt, update, SkLineType.STExpr);
	//
	// // return update;
	// }

	// public Object visitStmtIfThen(StmtIfThen stmt) {
	// if (currStmt == null || !(currStmt instanceof StmtIfThen))
	// return super.visitStmtIfThen(stmt);
	// Expression exp = ((StmtIfThen) currStmt).getCond();
	// if (!exp.toString().equals(stmt.getCond().toString()))
	// return super.visitStmtIfThen(stmt);
	//
	// Statement update = insertBlock(exp, stmt);
	// originCand.updateSkLineHole(stmt, update, SkLineType.STBLOCK);
	// // initCurrStmt();
	// return update;
	// }

	private Statement insertBlock(Expression invoker) {
		List<Statement> list = new ArrayList<Statement>();
		List<Statement> cons = new ArrayList<Statement>();
		// if (returnObj != null) {
		// Expression rhs = candGenerator.getStmtAssign(func.getName(),
		// returnObj.getType().toString());
		// StmtAssign assign = new StmtAssign(stmt.getOrigin(),
		// new ExprVar(stmt.getOrigin(), returnObj.getName()), rhs);
		// stmts.add(assign);
		// }
		cons.add(new StmtReturn(invoker.getOrigin(), null));
		StmtIfThen ifThen = new StmtIfThen(invoker.getOrigin(), invoker, new StmtBlock(invoker.getOrigin(), cons),
				null);
		list.add(ifThen);
		return ifThen;
	}

	@Override
	protected boolean hasDone() {
		return touchStmt.isEmpty();
	}

	public Object visitFunction(Function func) {
		if (currStmt == null)
			return super.visitFunction(func);
		List<Statement> lists = ((StmtBlock) func.getBody()).getStmts();
		List<Statement> newList = new ArrayList<Statement>();
		for (Statement stmt : lists) {
			// FIXME
			if (stmt.equals(currStmt)) {
//newList.add(insertBlock())
			}
		}

		return super.visitFunction(func);

	}

	// private void initCurrStmt() {
	//
	// if (!touchStmt.isEmpty()) {
	// currStmt = touchStmt.pop();
	// currSkStmt = allTouchStmts.get(--touchListID);
	// } else
	// currStmt = null;
	// }

}
