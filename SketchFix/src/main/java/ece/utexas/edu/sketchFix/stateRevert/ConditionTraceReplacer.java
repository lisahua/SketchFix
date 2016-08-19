/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtIfThen;


public class ConditionTraceReplacer extends FEReplacer {
	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();
	StmtIfThen atomIf = null;

	public ConditionTraceReplacer(List<ASTLinePy> assLines, List<ASTLinePy> codeLines) {
		allLines.addAll(assLines);
		allLines.addAll(codeLines);
		for (int i = codeLines.size() - 1; i >= 0; i--) {
			for (Statement stmt : codeLines.get(i).getSkStmts()) {
				if (stmt instanceof StmtIfThen) {
					atomIf = (StmtIfThen) stmt;
					return;
				}
			}
		}

	}

	public Object visitStmtIfThen(StmtIfThen ifStmt) {
		if (!atomIf.toString().equals(ifStmt.toString()))
			return super.visitStmtIfThen(ifStmt);
		int isTrue = isTrue(ifStmt);
		if (isTrue < 0) {
			return super.visitStmtIfThen(ifStmt);
		}
		ExprConstInt item = ExprConstInt.zero;
		if (isTrue > 0)
			item = ExprConstInt.one;
		return new StmtIfThen(ifStmt.getOrigin(), item, ifStmt.getCons(), ifStmt.getAlt());
	}

	private int isTrue(StmtIfThen ifStmt) {
		List<ASTLinePy> cand = getASTLinePy(ifStmt);
		if (cand.size() == 0)
			return -1;

		List<ASTLinePy> cons = getASTLinePy(ifStmt.getCons());
		List<ASTLinePy> alts = getASTLinePy(ifStmt.getAlt());
		if (cons.size() == 0 && alts.size() > 0)
			return 0;
		else if (cons.size() > 0) {
			// System.out.println(cons.get(1).getLinePyList().get(0).getLineNum()
			// + "-"
			// + cons.get(0).getLinePyList().get(0).getLineNum());
			// if (cons.get(1).getLinePyList().get(0).getLineNum() -
			// cons.get(0).getLinePyList().get(0).getLineNum() < 3)
			return 1;

		}
		return 0;
	}

	private List<ASTLinePy> getASTLinePy(Statement ifStmt) {

		List<ASTLinePy> candidates = new ArrayList<ASTLinePy>();
		if (ifStmt == null)
			return candidates;
		String ifStmtStr = ifStmt.toString().replace("\n", "").replace(" ", "").replace("\t", "");
		for (ASTLinePy line : allLines) {
			for (Statement stmt : line.getSkStmts()) {
				String sStr = stmt.toString().replace("\n", "").replace(" ", "").replace("\t", "");
				if (ifStmtStr.indexOf(sStr)>-1) {
					candidates.add(line);
					break;
				}
			}
		}
		return candidates;
	}
}
