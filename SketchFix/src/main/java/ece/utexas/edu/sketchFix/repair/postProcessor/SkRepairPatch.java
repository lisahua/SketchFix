/**
 * @author Lisa Sep 12, 2016 SkRepairPatch.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.stmts.Statement;

public class SkRepairPatch {

	TreeMap<Integer, List<SkLinePy>> insert = new TreeMap<Integer, List<SkLinePy>>();
	org.eclipse.jdt.core.dom.Statement origDOMStmt;
	List<SkLinePy> beforeRepair;
	HashMap<String, org.eclipse.jdt.core.dom.Statement> domMap = new HashMap<String, org.eclipse.jdt.core.dom.Statement>();

	public void setRepairItem(RepairItem repairItem) {
		List<ASTLinePy> list = repairItem.getStateList();
		beforeRepair = repairItem.beforeRepair;
		for (ASTLinePy ast : list) {
			for (Statement stmt : ast.getSkStmts()) {
				domMap.put(stmt.toString(), ast.getStatement());
			}
		}
	}

	public void setInsertMap(TreeMap<Integer, List<SkLinePy>> insert) {
		this.insert = insert;

	}

	public org.eclipse.jdt.core.dom.Statement getOrigDOMStmt() {
		if (origDOMStmt == null)
			findOrigDOMStmt();
		return origDOMStmt;
	}

	private void findOrigDOMStmt() {
		if (insert.size() == 0)
			return;
		int lastMatch = insert.lastKey();
		for (int i = lastMatch; i >= 0; i--) {
			FENode node = beforeRepair.get(i).getSkStmt();
			if (node instanceof Statement) {
				if (domMap.containsKey(node.toString())) {
					origDOMStmt = domMap.get(node.toString());
					return;
				}
			}
		}
	}

	public List<Statement> getDelta() {
		List<Statement> stmts = new ArrayList<Statement>();
		for (SkLinePy line : insert.get(insert.lastKey())) {
			FENode node = line.getSkStmt();
			if (node instanceof Statement)
				stmts.add((Statement) node);
		}
		return stmts;
	}

}
