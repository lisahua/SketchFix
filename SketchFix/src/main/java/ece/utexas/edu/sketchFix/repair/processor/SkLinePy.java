/**
 * @author Lisa Aug 20, 2016 SkLinePy.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.HashSet;
import java.util.TreeMap;

import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.stmts.StmtAssert;

public class SkLinePy {
	public SkLinePy(String string, FENode stmt, SkLineType type) {
		lineString = string;
		skStmt = stmt;
		this.type = type;
	}

	String func;
	int lineNo;
	String lineString;
	FENode skStmt;
	TreeMap<String, HashSet<String>> availExprs = new TreeMap<String, HashSet<String>>();
	boolean isHole = false;
	SkLineType type;

	public String toString() {
		return lineString;
	}

	public int getAssLine() {
		if (skStmt instanceof StmtAssert) {
			if (lineString.contains("//Assert at ")) {
				String[] tokens = lineString.split(":");
				String line = tokens[tokens.length - 1];
				line = line.substring(0, line.indexOf("("));
				int index = 0;
				try {
					index = Integer.parseInt(line.trim());
				} catch (Exception e) {

				}
				return index;
			}
		}
		return -1;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	public String getLineString() {
		return lineString;
	}

	public void setLineString(String lineString) {
		this.lineString = lineString;
	}

	public FENode getSkStmt() {
		return skStmt;
	}

	public void setSkStmt(FENode skStmt) {
		this.skStmt = skStmt;
	}

	public TreeMap<String, HashSet<String>> getAvailExprs() {
		return availExprs;
	}

	public void setAvailExprs(TreeMap<String, HashSet<String>> availExprs) {
		this.availExprs = availExprs;
	}

	public void setHole(boolean b) {
		isHole = b;
	}

	public boolean isHole() {
		return isHole;
	}

	public SkLineType getType() {
		return type;
	}

	public void setType(SkLineType type) {
		this.type = type;
	}

}
