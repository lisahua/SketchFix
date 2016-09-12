/**
 * @author Lisa Aug 19, 2016 NullExceptionHandler.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.postProcessor.RepairOpType;
import ece.utexas.edu.sketchFix.repair.processor.CandidateTemplate;
import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtFor;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.stmts.StmtWhile;

public class NullExceptionHandler extends CandidateTemplate {
	// ExprFunCall lastCall = null;
	Parameter returnObj = null;

	public NullExceptionHandler(SkCandidate generator) {
		super(generator);
		init();
	}

	protected void init() {

		List<Statement> allTouchStmts = new ArrayList<Statement>();
			for (Statement stmt : originCand.getAllTouchStatement())
				allTouchStmts.add(stmt);
		
		List<Statement> insertStmt = new ArrayList<Statement>();
		HashMap<Expression, Integer> inv = originCand.getInvariantMap();
		for (Expression exp : inv.keySet()) {
			if (inv.get(exp) > 0) {
				insertStmt.add(insertBlock(exp));
			}
		}
		List<Statement> stmts = ((StmtBlock) originCand.getCurrentFunc().getBody()).getStmts();
		List<Statement> tmp = new ArrayList<Statement>();
		for (Statement stmt : allTouchStmts) {
			int index = stmts.indexOf(stmt);
			if (index>-1) {
				for (Statement insert : insertStmt) {
					repairItems.add(new RepairItem(index, stmt, originCand.getCurrentFunc(), insert, RepairOpType.ADDBEFORE));
					tmp.add(stmt);
				}
			}
		}
		allTouchStmts.removeAll(tmp);
		tmp.clear();
		for (Statement line : stmts) {
			if (line instanceof StmtWhile) {
				List<Statement> lines = ((StmtBlock) ((StmtWhile) line).getBody()).getStmts();
				for (Statement stmt : allTouchStmts) {
					int index = stmts.indexOf(stmt);
					if (index>-1) {
						for (Statement insert : insertStmt) {
							repairItems.add(new RepairItem(index,stmt, line, insert, RepairOpType.ADDBEFORE));
							tmp.add(stmt);
						}
					}
				}
			} else if (line instanceof StmtFor) {
				List<Statement> lines = ((StmtBlock) ((StmtFor) line).getBody()).getStmts();
				for (Statement stmt : allTouchStmts) {
					int index = stmts.indexOf(stmt);
					if (index>-1) {
						for (Statement insert : insertStmt) {
							repairItems.add(new RepairItem(index, stmt, line, insert, RepairOpType.ADDBEFORE));
							tmp.add(stmt);
						}
					}
				}
			} else if (line instanceof StmtIfThen) {
				List<Statement> lines = ((StmtBlock) ((StmtIfThen) line).getCons()).getStmts();
				if (((StmtIfThen) line).getAlt() != null)
					lines.addAll(((StmtBlock) ((StmtIfThen) line).getAlt()).getStmts());

				for (Statement stmt : allTouchStmts) {
					int index = stmts.indexOf(stmt);
					if (index>-1) {
						for (Statement insert : insertStmt) {
							repairItems.add(new RepairItem(index, stmt, line, insert, RepairOpType.ADDBEFORE));
							tmp.add(stmt);
						}
					}
				}
			}
		}
	}

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

	// @Override
	// protected boolean hasDone() {
	// return repairItems.isEmpty();
	// }

}
