/**
 * @author Lisa Aug 20, 2016 SkLinePy.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.HashSet;
import java.util.TreeMap;

import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.stmts.Statement;

public class SkLinePy {
	public SkLinePy(String string, FENode stmt) {
		// TODO Auto-generated constructor stub
	}
	String func;
	int lineNo;
	String lineString;
	Statement skStmt;
	TreeMap<String, HashSet<String>> availExprs = new TreeMap<String, HashSet<String>>();
	
	

}
