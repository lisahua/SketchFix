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
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtVarDecl;

public class ConditionTraceReplacer extends FEReplacer {

	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();
	// StmtExpr atomCall = null;
	// int lastCallID = 0;
	// String state;
	HashMap<Expression, Integer> ifExpr = new HashMap<Expression, Integer>();

	public ConditionTraceReplacer(List<ASTLinePy> allLines, Function data) {
		this.allLines = allLines;
		// String methodName = data.getName();
		// if (methodName.contains("_"))
		// methodName = methodName.substring(0, methodName.indexOf("_"));
		StmtIfThen currIf = null;
		for (int i = 0; i < allLines.size(); i++) {
			for (Statement stmt : allLines.get(i).getSkStmts()) {
				if (stmt instanceof StmtIfThen) {
					if (currIf != null)
						ifExpr.put(currIf.getCond(), -1);
					currIf = (StmtIfThen) stmt;
				} else if (currIf != null) {
					Statement cons = currIf.getCons();
					if (cons instanceof StmtBlock) {
						List<Statement> list = ((StmtBlock) cons).getStmts();
						for (Statement s : list) {
							if (s instanceof StmtAssign && stmt instanceof StmtAssign) {
								if (((StmtAssign) s).getRHS().equals(((StmtAssign) stmt).getRHS())) {
									ifExpr.put(currIf.getCond(), 1);
									currIf = null;
									break;
								}

							} else if (s instanceof StmtVarDecl && stmt instanceof StmtVarDecl) {
								if (((StmtVarDecl) s).getType(0).toString()
										.equals(((StmtVarDecl) stmt).getType(0).toString())) {
									ifExpr.put(currIf.getCond(), 1);
									currIf = null;
									break;
								}
								ifExpr.put(currIf.getCond(), 1);
								currIf = null;
								break;
							}
						}
					}

				}
			}
		}
	}

	public Object visitStmtIfThen(StmtIfThen stmt) {
		try {
			if (!ifExpr.containsKey(stmt.getCond()))
				return super.visitStmtIfThen(stmt);
		} catch (Exception e) {
			return super.visitStmtIfThen(stmt);
		}
		Expression exp = stmt.getCond();
		if (!(exp instanceof ExprBinary))
			return super.visitStmtIfThen(stmt);
		ExprBinary bin = (ExprBinary) exp;
		StmtAssign assign = null;
		if (bin.getOp() == ExprBinary.BINOP_EQ && ifExpr.get(stmt.getCond()) == 1) {
			assign = new StmtAssign(stmt.getOrigin(), bin.getLeft(), bin.getRight());
		} else if (bin.getOp() == ExprBinary.BINOP_NEQ && ifExpr.get(stmt.getCond()) == -1) {
			assign = new StmtAssign(stmt.getOrigin(), bin.getLeft(), bin.getRight());
		}
		if (assign == null)
			return super.visitStmtIfThen(stmt);
		StmtIfThen newIf = new StmtIfThen(stmt.getOrigin(), ExprConstInt.one, stmt.getCons(), stmt.getAlt());

		List<Statement> list = new ArrayList<Statement>();
		list.add(assign);
		list.add(newIf);
		StmtBlock block = new StmtBlock(stmt.getOrigin(), list);
		return block;
	}

	// public Object visitStmtAssign(StmtAssign stmt) {
	// List<ASTLinePy> lines = isTouched(stmt);
	// if (lines.size() == 0 || !(stmt instanceof StmtAssign))
	// return super.visitStmtAssign(stmt);
	//
	// Expression lhs = stmt.getLHS();
	// List<Statement> list = new ArrayList<Statement>();
	// list.add(stmt);
	//
	// StringBuilder builder = new StringBuilder();
	// for (ASTLinePy line : lines)
	// builder.append(line.getStateIfAny());
	// String state = builder.toString();
	// if (state.trim().length() == 0)
	// return super.visitStmtAssign(stmt);
	// if ((lhs instanceof ExprVar) && state.equals("null")) {
	// StmtAssign assign = new StmtAssign(stmt.getOrigin(), lhs,
	// ExprNullPtr.nullPtr);
	// list.add(assign);
	// }
	// return new StmtBlock(stmt.getOrigin(), list);
	// }

	// private String getState(int i) {
	// for (; i >= 0; i--) {
	// String state = allLines.get(i).getState();
	// if (state.length() > 0)
	// return state;
	// }
	// return "";
	// }

	// private List<ASTLinePy> isTouched(StmtAssign stmt) {
	// List<ASTLinePy> candidates = new ArrayList<ASTLinePy>();
	// for (ASTLinePy line : allLines) {
	// for (Statement st : line.getSkStmts()) {
	// if (st instanceof StmtAssign) {
	// if (((StmtAssign)
	// st).getLHS().toString().equals(stmt.getLHS().toString()))
	// candidates.add(line);
	// }
	// }
	// }
	// return candidates;
	// }

	public HashMap<Expression, Integer> getTraceInvariant() {
		return ifExpr;
	}
}
