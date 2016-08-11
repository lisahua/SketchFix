/**
 * @author Lisa Jul 25, 2016 LinePy.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.instrModel.InstrPy;

public class LinePy {
	private String filePath = "";
	private String methodName = "";
	private int lineNum = 0;
	private String toString = "";
	private String sourceLine = "";
	private Vector<StringBuilder> storeState = new Vector<StringBuilder>();
	private Vector<InstrPy> instructions = new Vector<InstrPy>();
	private String prevType = "";
	private String params;

	public LinePy(String line) {
		String[] tokens = line.split("-");
		filePath = tokens[0];
		methodName = tokens[1];
		params = tokens[2];
		lineNum = Integer.parseInt(tokens[3]);
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

	/**
	 * Use a vector of states because one line of code may store multiple
	 * objects, although it's rare.
	 * 
	 * @param line
	 */
	public void insertState(String line) {
		if (storeState.size() == 0) {
			storeState.add(new StringBuilder());
		}
		StringBuilder currentObj = storeState.get(storeState.size() - 1);
		// TODO buggy?
		currentObj.append(line);

	}

	public void startNewState() {
		storeState.add(new StringBuilder());
	}

	public Object getStoreState() {
		// TODO
		return null;
	}

	public String getStoreStateString() {
		StringBuilder builder = new StringBuilder();
		for (StringBuilder sb : storeState)
			builder.append(sb);
		return builder.toString();
	}

	public void addInstruction(InstrPy instr) {

		if (storeState.size() > 0) {
			// FIXME bug if multi states
			String sb = storeState.get(0).toString().replace("\"", "").replace("\n", "");
			String type = instr.getInstType();
			if (type.equals("ISTORE")) {
				// FIXME boolean
				instr.setStoreState(Integer.parseInt(sb));
			} else if (type.equals("DSTORE")) {
				instr.setStoreState(Double.parseDouble(sb));
			} else if (type.equals("FSTORE")) {
				instr.setStoreState(Float.parseFloat(sb));
			} else if (type.equals("LSTORE")) {
				instr.setStoreState(Long.parseLong(sb));
			} else if (type.equals("ASTORE") || type.equals("PUTSTATIC") || type.equals("PUTFIELD")) {
				instr.setStoreState(sb, prevType);
			} else
				parseType(instr);
		}
		instructions.add(instr);

	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	private void parseType(InstrPy instr) {
		// FIXME hacky
		if (instr.getVarType().trim().length() > prevType.length())
			prevType = instr.getVarType();
		// TODO has to map back to legal type name for reflection
	}

	public String getSourceLine() {
		return sourceLine;
	}

	public void setSourceLine(String sourceLine) {
		this.sourceLine = sourceLine;
	}

	public Vector<InstrPy> getInstructions() {
		return instructions;
	}

}
