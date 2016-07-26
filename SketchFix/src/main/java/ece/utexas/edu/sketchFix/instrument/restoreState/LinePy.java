/**
 * @author Lisa Jul 25, 2016 LinePy.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

public class LinePy {
	private String filePath = "";
	private String methodName = "";
	private int lineNum = 0;
	private String toString = "";
	// I dont like it, change to enum or invoke asm methods to convert to enum
	private String instType = "";
	private String varType = "";
	private String instSecond = "";

	public LinePy(String line) {
		String[] tokens = line.split("-");
		filePath = tokens[0];
		methodName = tokens[1];
		lineNum = Integer.parseInt(tokens[2]);
		toString = line;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public String toString() {
		return toString;
	}

	public String getToString() {
		return toString;
	}

	public void setToString(String toString) {
		this.toString = toString;
	}

	public String getInstType() {
		return instType;
	}

	public void setInstType(String instType) {
		this.instType = instType;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

	public String getInstSecond() {
		return instSecond;
	}

	public void setInstSecond(String instSecond) {
		this.instSecond = instSecond;
	}

}
