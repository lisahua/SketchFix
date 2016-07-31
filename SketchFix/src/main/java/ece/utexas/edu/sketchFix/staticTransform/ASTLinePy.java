/**
 * @author Lisa Jul 31, 2016 ASTLinePy.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Statement;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;

public class ASTLinePy {

	List<LinePy> linePyList = new ArrayList<LinePy>();
	Statement statement;

	public ASTLinePy(LinePy linePy, Statement stmt) {
		linePyList.add(linePy);
		statement = stmt;
	}

	public void addLinePy(LinePy linePy) {
		linePyList.add(linePy);
	}
}
