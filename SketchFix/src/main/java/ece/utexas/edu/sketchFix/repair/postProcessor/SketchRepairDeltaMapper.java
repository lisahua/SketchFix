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

public class SketchRepairDeltaMapper {
	// List<ASTLinePy> lists;
	Function func;
	SkLinePy hole;
	List<SkLinePy> beforeRepair;
	ASTLinePy astHole;

	public SketchRepairDeltaMapper(List<ASTLinePy> lists, List<SkLinePy> beforeRepair) {
		mapBefore(lists, beforeRepair);
		this.beforeRepair = beforeRepair;
	}

	public SketchToDOMTransformer setNewScope(List<SkLinePy> scope) {
		return mapNewScope(scope, beforeRepair);
	}

	@Deprecated
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

	private SketchToDOMTransformer mapNewScope(List<SkLinePy> scope, List<SkLinePy> beforeRepair) {
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

	private SketchToDOMTransformer startMappingFuncs(List<SkLinePy> scope, List<SkLinePy> beforeRepair) {
		List<SkLinePy> holes = new ArrayList<SkLinePy>();
		List<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < scope.size(); i++) {
			for (int j = 0; j < beforeRepair.size(); j++) {
				if (matchTwoSkLinePy(scope.get(i), beforeRepair.get(j))) {
					// if (scopeMatch[i] != 0)
					// continue;
					// scopeMatch[i] = j;
					if (beforeRepair.get(j).isHole()) {
						ids.add(i);
					}
					// System.out.println(i + "-" + j + " (" + isHole + "):" +
					// scope.get(i) + "--" + beforeRepair.get(j));
				}
			}
		}
		if (ids.size() > 1) {
			for (int i = ids.get(0); i <= ids.get(1); i++)
				holes.add(scope.get(i));
		} else if (ids.size() == 1)
			holes.add(scope.get(ids.get(0)));
		holes = convertHolesToSoln(holes);
		return new SketchToDOMTransformer(func, findDelta(holes), astHole);
	}

	private List<SkLinePy> convertHolesToSoln(List<SkLinePy> holes) {
		List<SkLinePy> result = new ArrayList<SkLinePy>();
		if (holes.get(0).getSkStmt() instanceof StmtIfThen) {
			StmtIfThen ifStmt = (StmtIfThen) holes.get(0).getSkStmt();
			Statement condStmt = ifStmt.getCons();
			Statement altStmt = ifStmt.getAlt();
			List<Statement> newList = convertStmt(condStmt);
			newList.addAll(convertStmt(altStmt));
			List<Statement> oldList = new ArrayList<Statement>();
			for (int i = 1; i < holes.size() - 1; i++) {
				oldList.addAll(convertStmt((Statement) holes.get(i).getSkStmt()));
			}
			StmtBlock block = new StmtBlock(ifStmt.getOrigin(), oldList);
			StmtIfThen newIf = new StmtIfThen(ifStmt.getOrigin(), ifStmt.getCond(), block, ifStmt.getAlt());
			holes.get(0).setSkStmt(newIf);
			result.add(holes.get(0));
			result.add(holes.get(holes.size() - 1));
		}

		return result;

	}

	private List<Statement> convertStmt(Statement stmt) {
		List<Statement> newList = new ArrayList<Statement>();
		if (stmt instanceof StmtBlock)
			newList.addAll(((StmtBlock) stmt).getStmts());
		else
			newList.add(stmt);
		return newList;
	}

	private List<Statement> findDelta(List<SkLinePy> isHole) {

		List<Statement> newList = new ArrayList<Statement>();
		if (astHole == null)
			return newList;
		List<Statement> oldList = astHole.getSkStmts();

		for (SkLinePy newLine : isHole) {
			if (newLine.getType().equals(SkLineType.STBLOCK))
				newList.addAll(((StmtBlock) newLine.getSkStmt()).getStmts());
			else
				newList.add((Statement) newLine.getSkStmt());
		}

		return findDelta(newList, oldList);

	}

	private List<Statement> findDelta(List<Statement> newList, List<Statement> oldList) {
		List<Statement> newDeltaList = new ArrayList<Statement>();
		newDeltaList.addAll(newList);
		List<Statement> oldDeltaList = new ArrayList<Statement>();
		oldDeltaList.addAll(oldList);

		for (Statement oldS : oldList) {
			for (Statement newS : newList) {
				if (oldS instanceof StmtExpr && newS instanceof StmtExpr) {
					if (((StmtExpr) newS).getExpression().toString()
							.equals(((StmtExpr) oldS).getExpression().toString())) {
						oldDeltaList.remove(oldS);
						newDeltaList.remove(newS);
						break;
					}
				} else if (oldS instanceof StmtIfThen && newS instanceof StmtIfThen) {
					if (((StmtIfThen) newS).getCond().toString().equals(((StmtIfThen) oldS).getCond().toString())) {
						oldDeltaList.remove(oldS);
						newDeltaList.remove(newS);
						break;
					}
				} else if (oldS instanceof StmtWhile && newS instanceof StmtWhile) {
					if (((StmtWhile) newS).getCond().toString().equals(((StmtWhile) oldS).getCond().toString())) {
						oldDeltaList.remove(oldS);
						newDeltaList.remove(newS);
						break;
					}
				} else if (oldS instanceof StmtVarDecl && newS instanceof StmtVarDecl) {
					if (((StmtVarDecl) newS).getName(0).toString().equals(((StmtVarDecl) oldS).getName(0).toString())) {
						oldDeltaList.remove(oldS);
						newDeltaList.remove(newS);
						break;
					}
				} else if (oldS instanceof StmtAssert && newS instanceof StmtAssert) {
					if (((StmtAssert) newS).getCond().toString().equals(((StmtAssert) oldS).getCond().toString())) {
						oldDeltaList.remove(oldS);
						newDeltaList.remove(newS);
						break;
					}
				}
			}

		}
		// newDeltaList.addAll(oldDeltaList);
		return newDeltaList;

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
