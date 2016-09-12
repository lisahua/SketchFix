/**
 * @author Lisa Aug 20, 2016 SkRepairMapper.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.repair.processor.SkLineType;
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

public class SkOutputDeltaMapper {
	Function func;
	List<SkLinePy> beforeRepair;
	SkRepairPatch skPatch = new SkRepairPatch();

	public SkOutputDeltaMapper(RepairItem repairItem) {
		this.beforeRepair = repairItem.getBeforeRepair();
		skPatch.setRepairItem(repairItem);
	}

	public SketchToDOMTransformer setNewScope(List<SkLinePy> scope) {
		return startMappingFuncs(scope, beforeRepair);
	}

	// FIXME to Longest common sequence algo
	private SketchToDOMTransformer startMappingFuncs(List<SkLinePy> scope, List<SkLinePy> beforeRepair) {
		TreeMap<Integer, List<SkLinePy>> insert = new TreeMap<Integer, List<SkLinePy>>();
		int lastMatchJ = 0;
		for (int i = 0; i < scope.size(); i++) {
			boolean flag = false;
			for (int j = 0; j < beforeRepair.size(); j++) {
				if (matchTwoSkLinePy(scope.get(i), beforeRepair.get(j))) {
					flag = true;
					lastMatchJ = j;
					break;
				}
			}
			if (!flag){
				List<SkLinePy> list = insert.containsKey(lastMatchJ)? insert.get(lastMatchJ): new ArrayList<SkLinePy>();
				list.add(scope.get(i));
				insert.put(lastMatchJ,list);
			}
		}
		skPatch.setInsertMap(insert);
		return new SketchToDOMTransformer(func, skPatch);
	}

//	private List<Statement> findDelta(List<SkLinePy> delta) {
//
//		List<Statement> newList = new ArrayList<Statement>();
//		if (astHole != null) {
//			List<Statement> oldList = astHole.getSkStmts();
//			for (SkLinePy newLine : delta) {
//				if (newLine.getType().equals(SkLineType.STBLOCK))
//					newList.addAll(((StmtBlock) newLine.getSkStmt()).getStmts());
//				else
//					newList.add((Statement) newLine.getSkStmt());
//			}
//			return findDelta(newList, oldList);
//		} else {
//			for (SkLinePy line : delta) {
//				newList.add((Statement) line.getSkStmt());
//			}
//
//		}
//
//	}

//	private List<Statement> findDelta(List<Statement> newList, List<Statement> oldList) {
//		List<Statement> newDeltaList = new ArrayList<Statement>();
//		newDeltaList.addAll(newList);
//		List<Statement> oldDeltaList = new ArrayList<Statement>();
//		oldDeltaList.addAll(oldList);
//
//		for (Statement oldS : oldList) {
//			for (Statement newS : newList) {
//				if (oldS instanceof StmtExpr && newS instanceof StmtExpr) {
//					if (((StmtExpr) newS).getExpression().toString()
//							.equals(((StmtExpr) oldS).getExpression().toString())) {
//						oldDeltaList.remove(oldS);
//						newDeltaList.remove(newS);
//						break;
//					}
//				} else if (oldS instanceof StmtIfThen && newS instanceof StmtIfThen) {
//					if (((StmtIfThen) newS).getCond().toString().equals(((StmtIfThen) oldS).getCond().toString())) {
//						oldDeltaList.remove(oldS);
//						newDeltaList.remove(newS);
//						break;
//					}
//				} else if (oldS instanceof StmtWhile && newS instanceof StmtWhile) {
//					if (((StmtWhile) newS).getCond().toString().equals(((StmtWhile) oldS).getCond().toString())) {
//						oldDeltaList.remove(oldS);
//						newDeltaList.remove(newS);
//						break;
//					}
//				} else if (oldS instanceof StmtVarDecl && newS instanceof StmtVarDecl) {
//					if (((StmtVarDecl) newS).getName(0).toString().equals(((StmtVarDecl) oldS).getName(0).toString())) {
//						oldDeltaList.remove(oldS);
//						newDeltaList.remove(newS);
//						break;
//					}
//				} else if (oldS instanceof StmtAssert && newS instanceof StmtAssert) {
//					if (((StmtAssert) newS).getCond().toString().equals(((StmtAssert) oldS).getCond().toString())) {
//						oldDeltaList.remove(oldS);
//						newDeltaList.remove(newS);
//						break;
//					}
//				}
//			}
//
//		}
//		// newDeltaList.addAll(oldDeltaList);
//		return newDeltaList;
//
//	}

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

}