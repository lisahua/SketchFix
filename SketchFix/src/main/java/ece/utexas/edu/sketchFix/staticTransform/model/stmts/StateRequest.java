/**
 * @author Lisa Aug 14, 2016 StateRequest.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import org.eclipse.jdt.core.dom.Statement;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;

public class StateRequest {
	String state = "";

	public StateRequest(String json, Statement stmt, ASTLinePy item) {
		this.state = item.getStateIfAny();
	}
}
