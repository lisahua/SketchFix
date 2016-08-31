/**
 * @author Lisa Jul 31, 2016 ASTLinePy.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import sketch.compiler.ast.core.stmts.Statement;

public class ASTLinePy {

	private List<LinePy> linePyList = new ArrayList<LinePy>();
	private org.eclipse.jdt.core.dom.Statement statement;
	private String firstLinePyString = "";
	private List<Statement> skStmts = new ArrayList<Statement>();
	private int firstLineNum = 0;
	private String type = "";
//private int centerSkStmtID = 0;
	public ASTLinePy() {

	}

	public void setFirstLineString(String line) {
		firstLinePyString = line;
	}

	public String getLinePyString() {
		return firstLinePyString;
	}

//	public ASTLinePy(LinePy linePy, org.eclipse.jdt.core.dom.Statement stmt) {
//		linePyList.add(linePy);
//		statement = stmt;
//	}

	public void addLinePy(LinePy linePy) {
		if (linePyList.size() == 0) {
			firstLinePyString = linePy.getSourceLine().replace(" ", "").replace("\n", "").replace("\t", "");
			firstLineNum = linePy.getLineNum();
		}
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
		if (this.statement != null && this.statement.toString().length() < statement.toString().length())
			return;

		this.statement = statement;
	}

	public String getStateIfAny() {
		StringBuilder builder = new StringBuilder();
		for (LinePy line : linePyList) {
			if (builder.toString().contains(line.getStoreStateString()))
				continue;
			builder.append(line.getStoreStateString());
		}
		return builder.toString();
	}

	public String toString() {
		if (skStmts.isEmpty())
		return "";
		return skStmts.toString();
	}

	public void setSkStmt(Object skStmt) {
		if (this.skStmts.size() > 0 && this.skStmts.toString().length() < skStmt.toString().length())
			return;

		if (skStmt instanceof Statement)
			skStmts.add((Statement) skStmt);
		else
			skStmts.addAll((List<Statement>) skStmt);
	}

	public int getFirstLineNum() {
		return firstLineNum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Statement> getSkStmts() {
		return skStmts;
	}
	
//	public Statement getCenterSkStmt() {
//		
//	}

}
