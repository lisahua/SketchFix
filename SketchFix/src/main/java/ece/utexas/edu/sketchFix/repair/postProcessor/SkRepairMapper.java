/**
 * @author Lisa Aug 20, 2016 SkRepairMapper.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.stmts.StmtWhile;

public class SkRepairMapper {
	// List<ASTLinePy> lists;
	Function func;
	SkLinePy hole;
	List<SkLinePy> beforeRepair;

	public SkRepairMapper(List<ASTLinePy> lists, List<SkLinePy> beforeRepair) {
		mapBefore(lists, beforeRepair);
		this.beforeRepair = beforeRepair;
	}

	public void setNewScope(List<SkLinePy> scope) {
		mapNewScope(scope, beforeRepair);
	}

	private void mapNewScope(List<SkLinePy> scope) {
		List<Statement> holeStmts = new ArrayList<Statement>();
		if (hole.getSkStmt() instanceof StmtBlock) {
			holeStmts.addAll(((StmtBlock) hole.getSkStmt()).getStmts());
		} else if (hole.getSkStmt() instanceof Statement) {
			holeStmts.add((Statement) hole.getSkStmt());
		}

		for (int i = 0; i < scope.size(); i++) {
			List<Statement> lineStmt = new ArrayList<Statement>();
			if (scope.get(i).getSkStmt() instanceof StmtBlock) {
				lineStmt.addAll(((StmtBlock) hole.getSkStmt()).getStmts());
			} else if (scope.get(i).getSkStmt() instanceof Statement) {
				lineStmt.add((Statement) hole.getSkStmt());
			}
			for (int j = holeStmts.size() - 1; j > -1; j--) {
				if (hasMatch(lineStmt, holeStmts.get(j))) {
					// return i;
				}
			}
		}
		// return -1;
	}

	private void mapNewScope(List<SkLinePy> scope, List<SkLinePy> beforeRepair) {
		int[] matchArr = new int[scope.size()];
		System.out.println(scope.size() + "-" + beforeRepair.size());
		for (int i = 0; i < scope.size(); i++) {
			// if (scope.get(i).getSkStmt() instanceof StmtBlock) {
			// lineStmt.addAll(((StmtBlock) hole.getSkStmt()).getStmts());
			// } else if (scope.get(i).getSkStmt() instanceof Statement) {
			// lineStmt.add((Statement) hole.getSkStmt());
			// }
			for (int j = 0; j < beforeRepair.size(); j++) {
				if (matchTwoSkLinePy(scope.get(i), beforeRepair.get(j))) {
					matchArr[i] = j;
				}
			}
		}
		// return -1;
	}

	private boolean matchTwoSkLinePy(SkLinePy newLine, SkLinePy oldLine) {
		if (!newLine.getType().equals(oldLine.getType()))
			return false;
		// FIXME
		FENode newNode = newLine.getSkStmt();
		FENode oldNode = oldLine.getSkStmt();
		switch (newLine.getType()) {
		case FUNC:
			if (((Function) newNode).getName().equals(((Function) oldNode).getName())) {
				return true;
			}
			break;
		case STExpr:
			if (((StmtExpr) newNode).getExpression().toString().equals(((StmtExpr) oldNode).getExpression().toString()))
				return true;
			break;
		case STIFTHEN:
			if (((StmtIfThen) newNode).getCond().toString().equals(((StmtIfThen) oldNode).getCond().toString()))
				return true;
			break;
		case STVAR:
			if (((StmtVarDecl) newNode).getName(0).toString().equals(((StmtVarDecl) oldNode).getName(0).toString()))
				return true;
			break;
		case STWHILE:
			if (((StmtWhile) newNode).getCond().toString().equals(((StmtWhile) oldNode).getCond().toString()))
				return true;
			break;
		case STASS:
			if (((StmtAssert) newNode).getCond().toString().equals(((StmtAssert) oldNode).getCond().toString()))
				return true;
			break;
		}

		return false;
	}

	private void mapBefore(List<ASTLinePy> lists, List<SkLinePy> scope) {
		for (int i = 0; i < scope.size(); i++) {
			FENode node = scope.get(i).getSkStmt();
			if (node instanceof Function) {
				func = (Function) node;
			} else if (scope.get(i).isHole()) {
				hole = scope.get(i);
				if (node instanceof StmtBlock) {
					List<Statement> stmts = ((StmtBlock) node).getStmts();
					ASTLinePy ast = matchDOMStmt(lists, stmts);
					// FIXME cannot matchs
					if (ast == null)
						return;
					// rewriteDOM(ast, node);
				}
			}
		}
	}

	private void rewriteDOM(ASTLinePy ast, FENode newStmt) {
		org.eclipse.jdt.core.dom.Statement origDomStmt = ast.getStatement();
		List<Statement> astSkStmts = ast.getSkStmts();
		if (newStmt instanceof StmtBlock) {
			// List<Statement> newStmts =
		}
	}

	private ASTLinePy matchDOMStmt(List<ASTLinePy> astLines, List<Statement> stmts) {
		for (Statement stmt : stmts) {
			ASTLinePy ast = matchDOMStmt(astLines, stmt);
			if (ast != null)
				return ast;
		}
		return null;
	}

	private ASTLinePy matchDOMStmt(List<ASTLinePy> astLines, Statement oneStmt) {
		for (ASTLinePy ast : astLines) {
			List<Statement> stmts = ast.getSkStmts();
			if (hasMatch(stmts, oneStmt))
				return ast;
		}
		return null;
	}

	private boolean hasMatch(List<Statement> stmts, Statement oneStmt) {
		for (Statement st : stmts) {
			// FIXME buggy
			if (st.toString().equals(oneStmt.toString())) {
				return true;
			}
		}
		return false;
	}
}
