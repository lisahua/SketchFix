/**
 * @author Lisa Aug 14, 2016 StateRequest.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.Vector;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.stmts.Statement;

public class StateRequest {
	Vector<StmtStateItem> stateItem = new Vector<StmtStateItem>();

	public StateRequest() {

	}

	public void insert(String type, org.eclipse.jdt.core.dom.Statement stmt, ASTLinePy item, Statement skStmt) {
		stateItem.add(new StmtStateItem(type, stmt, item,skStmt));
	}
}

class StmtStateItem {
	String type;
	org.eclipse.jdt.core.dom.Statement stmt;
	ASTLinePy item;
	Statement skStmt;

	public StmtStateItem(String type, org.eclipse.jdt.core.dom.Statement stmt, ASTLinePy item, Statement skStmt) {
		this.type = type;
		this.stmt = stmt;
		this.item = item;
		this.skStmt = skStmt;
	}

}
