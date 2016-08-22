/**
 * @author Lisa Aug 22, 2016 RepairPatch.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import org.eclipse.jdt.core.dom.Statement;

import sketch.compiler.ast.core.stmts.StmtBlock;

public class RepairPatch {

	RepairOpType type;
	Statement originStmt;

	public void getUpdatedNode(Statement originStmt, Statement newStmt, RepairOpType type) {
		switch (type) {
		case ADD:

			break;
		case DELETE:
			break;
		case REPLACE:
			break;
		}
	}

}
