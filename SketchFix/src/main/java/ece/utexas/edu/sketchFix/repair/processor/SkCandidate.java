/**
 * @author Lisa Aug 26, 2016 SkCandidateList.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.Program;

public class SkCandidate {

	Program prog;
	List<SkLinePy> beforeRepair;
	String outputFile;
	List<ASTLinePy> states;
	String repairFile;

	public SkCandidate(Program prog, List<SkLinePy> lines, List<ASTLinePy> list) {

		this.prog = prog;
		this.beforeRepair = lines;
		this.states = list;
	}

	public Program getProg() {
		return prog;
	}

	public void setProg(Program prog) {
		this.prog = prog;
	}

	public List<SkLinePy> getBeforeRepair() {
		return beforeRepair;
	}

	public void setBeforeRepair(List<SkLinePy> beforeRepair) {
		this.beforeRepair = beforeRepair;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public List<ASTLinePy> getStates() {
		return states;
	}

	public void setStates(List<ASTLinePy> states) {
		this.states = states;
	}

	public String getFileAbsolutePath() {
		return repairFile;
	}

	public void setBaseDir(String dir) {
		String className = "";
		if (states != null && states.size() > 0)
			className = states.get(0).getLinePyList().get(0).getFilePath();
		repairFile = dir + className;
	}

}
