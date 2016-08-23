/**
 * @author Lisa Aug 22, 2016 RepairPatch.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Statement;

public class RepairPatch {

	RepairOpType type;
	Statement originStmt;
	List<Statement> repairs = new ArrayList<Statement>();
	Block origBlock;
	String origSB = "";
	// Block replaceBlock;
	int insertPoint = 0;
	AST ast;

	public RepairPatch(Block originStmt, AST ast) {
		this.origBlock = originStmt;
		origSB = origBlock.toString();
	}

	public void setInsertPoint(int i) {
		insertPoint = i;
		originStmt = (Statement) origBlock.statements().get(i);
	}

	public void insertStatement(Statement stmt) {
		repairs.add(stmt);
	}

	public RepairOpType getType() {
		return type;
	}

	public void setType(RepairOpType type) {
		this.type = type;
	}

	public Statement getOriginStmt() {
		return originStmt;
	}

	public void setOriginStmt(Statement originStmt) {
		this.originStmt = originStmt;
	}

	public List<Statement> getRepairs() {
		return repairs;
	}

	public void setRepairs(List<Statement> repairs) {
		this.repairs = repairs;
	}

	public String replaceBody(String str) {

		String part1 = str.substring(0, originStmt.getStartPosition());
		String indent = str.substring(part1.lastIndexOf("\n"), originStmt.getStartPosition());
		part1 += "//SketchFix:Start" + getAddString(indent);
		part1 += str.substring(originStmt.getStartPosition());

		return part1;
	}

	public String getAddString(String indent) {
		StringBuilder builder = new StringBuilder();
		for (Statement stmt : repairs)
			builder.append(stmt.toString());
		String[] lines = builder.toString().split("\n");
		builder = new StringBuilder();
		for (String line : lines)
			builder.append(indent + line);
		builder.append("//SketchFix:End");
		return builder.toString() + indent;
	}

}
