/**
 * @author Lisa Aug 20, 2016 SkRepairMapper.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.repair.processor.SkLineType;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtAssign;
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
	ASTLinePy astHole;

	public SkRepairMapper(List<ASTLinePy> lists, List<SkLinePy> beforeRepair) {
		mapBefore(lists, beforeRepair);
		this.beforeRepair = beforeRepair;
	}

	public RepairTransformer setNewScope(List<SkLinePy> scope) {
		return mapNewScope(scope, beforeRepair);
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

	private RepairTransformer mapNewScope(List<SkLinePy> scope, List<SkLinePy> beforeRepair) {
		// System.out.println(scope.size() + "-" + beforeRepair.size());
		for (int i = 0; i < scope.size(); i++) {
			for (int j = 0; j < beforeRepair.size(); j++) {
				if (matchTwoSkLinePy(scope.get(i), beforeRepair.get(j))) {
					return startMappingFuncs(scope.subList(i, scope.size()),
							beforeRepair.subList(j, beforeRepair.size()));
					// System.out.println(i + "-" + j + ":" + scope.get(i) +
					// "--" + beforeRepair.get(j));

				}
			}
		}
		return null;
		// System.out.println(matchArr);
	}

	private RepairTransformer startMappingFuncs(List<SkLinePy> scope, List<SkLinePy> beforeRepair) {
		SkLinePy[] isHole = new SkLinePy[2];
		int[] scopeMatch = new int[scope.size()];
		// RepairTransformer transformer = new RepairTransformer();
		for (int i = 0; i < scope.size(); i++) {
			for (int j = 0; j < beforeRepair.size(); j++) {
				if (matchTwoSkLinePy(scope.get(i), beforeRepair.get(j))) {
					scopeMatch[i] = j;
					if (beforeRepair.get(j).isHole()) {
						isHole[0] = scope.get(i);
						isHole[1] = beforeRepair.get(j);
					}
					System.out.println(i + "-" + j + " (" + isHole + "):" + scope.get(i) + "--" + beforeRepair.get(j));
				}
			}
		}
		return new RepairTransformer(func, isHole, astHole);
	}

	private boolean matchTwoSkLinePy(SkLinePy newLine, SkLinePy oldLine) {
		if (!newLine.getType().equals(SkLineType.STBLOCK) && !oldLine.getType().equals(SkLineType.STBLOCK)) {
			if (!newLine.getType().equals(oldLine.getType()))
				return false;
			return atomMatchTwoSkLinePy(newLine.getType(), newLine.getSkStmt(), oldLine.getSkStmt());
		}
		if (newLine.getType().equals(SkLineType.STBLOCK) && oldLine.getType().equals(SkLineType.FUNC))
			return false;
		if (newLine.getType().equals(SkLineType.FUNC) && oldLine.getType().equals(SkLineType.STBLOCK))
			return false;
		List<Statement> newList = new ArrayList<Statement>();
		List<Statement> oldList = new ArrayList<Statement>();
		if (newLine.getType().equals(SkLineType.STBLOCK))
			newList.addAll(((StmtBlock) newLine.getSkStmt()).getStmts());
		else
			newList.add((Statement) newLine.getSkStmt());
		if (oldLine.getType().equals(SkLineType.STBLOCK))
			oldList.addAll(((StmtBlock) oldLine.getSkStmt()).getStmts());
		else
			oldList.add((Statement) oldLine.getSkStmt());
		for (Statement oldS : oldList) {
			for (Statement newS : newList) {
				if (oldS instanceof StmtExpr && newS instanceof StmtExpr) {
					if (((StmtExpr) newS).getExpression().toString()
							.equals(((StmtExpr) oldS).getExpression().toString()))
						return true;
				} else if (oldS instanceof StmtIfThen && newS instanceof StmtIfThen) {
					if (((StmtIfThen) newS).getCond().toString().equals(((StmtIfThen) oldS).getCond().toString()))
						return true;
				} else if (oldS instanceof StmtWhile && newS instanceof StmtWhile) {
					if (((StmtWhile) newS).getCond().toString().equals(((StmtWhile) oldS).getCond().toString()))
						return true;
				} else if (oldS instanceof StmtVarDecl && newS instanceof StmtVarDecl) {
					if (((StmtVarDecl) newS).getName(0).toString().equals(((StmtVarDecl) oldS).getName(0).toString()))
						return true;
				} else if (oldS instanceof StmtAssert && newS instanceof StmtAssert) {
					if (((StmtAssert) newS).getCond().toString().equals(((StmtAssert) oldS).getCond().toString()))
						return true;
				}

			}
		}

		return false;
	}

	private boolean atomMatchTwoSkLinePy(SkLineType type, FENode newNode, FENode oldNode) {
		switch (type) {
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
		case STRTN:
			return true;
		case STASSIGN:
			if (((StmtAssign) newNode).getLHS().toString().equals(((StmtAssign) oldNode).getLHS().toString())
					&& ((StmtAssign) newNode).getRHS().toString().equals(((StmtAssign) oldNode).getRHS().toString()))
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
					astHole = matchDOMStmt(lists, stmts);
					// FIXME cannot matchs
					if (astHole == null)
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
