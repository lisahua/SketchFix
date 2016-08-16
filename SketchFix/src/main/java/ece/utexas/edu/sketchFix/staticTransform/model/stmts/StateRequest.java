/**
 * @author Lisa Aug 14, 2016 StateRequest.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.Vector;

import org.eclipse.jdt.core.dom.Statement;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;

public class StateRequest {
	Vector<StmtStateItem> stateItem = new Vector<StmtStateItem>();

	public StateRequest() {

	}

	public void insert(String type, Statement stmt, ASTLinePy item) {
		stateItem.add(new StmtStateItem(type, stmt, item));
	}
}

class StmtStateItem {
	String type;
	Statement stmt;
	ASTLinePy item;

	public StmtStateItem(String type, Statement stmt, ASTLinePy item) {
		this.type = type;
		this.stmt = stmt;
		this.item = item;
	}

}
