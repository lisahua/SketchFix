/**
 * @author Lisa Aug 30, 2016 RepairItem.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import ece.utexas.edu.sketchFix.repair.postProcessor.RepairOpType;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.stmts.Statement;

public class RepairItem {

	Statement insertPoint;
	FENode scope;
	Statement replaceStmt;
	RepairOpType repairType;

	public RepairItem(Statement insertPoint, FENode scope, Statement replaceStmt, RepairOpType repairType) {
		this.insertPoint = insertPoint;
		this.scope = scope;
		this.replaceStmt = replaceStmt;
		this.repairType = repairType;
	}

	public Statement getInsertPoint() {
		return insertPoint;
	}

	public void setInsertPoint(Statement insertPoint) {
		this.insertPoint = insertPoint;
	}

	public FENode getScope() {
		return scope;
	}

	public void setScope(FENode scope) {
		this.scope = scope;
	}

	public Statement getReplaceStmt() {
		return replaceStmt;
	}

	public void setReplaceStmt(Statement replaceStmt) {
		this.replaceStmt = replaceStmt;
	}

	public RepairOpType getRepairType() {
		return repairType;
	}

	public void setRepairType(RepairOpType repairType) {
		this.repairType = repairType;
	}

}
