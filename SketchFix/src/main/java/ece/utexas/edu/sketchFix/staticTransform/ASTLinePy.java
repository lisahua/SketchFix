/**
 * @author Lisa Jul 31, 2016 ASTLinePy.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Statement;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;

public class ASTLinePy {

	private List<LinePy> linePyList = new ArrayList<LinePy>();
	private org.eclipse.jdt.core.dom.Statement statement;
	

	public ASTLinePy(LinePy linePy, Statement stmt) {
		linePyList.add(linePy);
		statement = stmt;
	}

	public void addLinePy(LinePy linePy) {
		linePyList.add(linePy);
	}

	public List<LinePy> getLinePyList() {
		return linePyList;
	}

	public void setLinePyList(List<LinePy> linePyList) {
		this.linePyList = linePyList;
	}

	public org.eclipse.jdt.core.dom.Statement getStatement() {
		return statement;
	}

	public void setStatement(org.eclipse.jdt.core.dom.Statement statement) {
		this.statement = statement;
	}
	
	
}
