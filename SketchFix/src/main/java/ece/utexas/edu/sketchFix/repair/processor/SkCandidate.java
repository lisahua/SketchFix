/**
 * @author Lisa Aug 26, 2016 SkCandidateList.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.List;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.Program;

public class SkCandidate {

	Program prog;
	List<SkLinePy> beforeRepair;
	String outputFile;
	List<ASTLinePy> states;
	String repairFile;
	MethodData methodData;
	public SkCandidate(Program prog, List<SkLinePy> lines, List<ASTLinePy> list, MethodData methodData) {

		this.prog = prog;
		this.beforeRepair = lines;
		this.states = list;
		this.methodData = methodData;
	}

	public MethodData getMethodData() {
		return methodData;
	}

	public void setMethodData(MethodData methodData) {
		this.methodData = methodData;
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
		return methodData.getClassAbsolutePath();
	}

//	public void setBaseDir(String dir) {
//		String className = "";
//		if (states != null && states.size() > 0)
//			className = states.get(states.size()-1).getLinePyList().get(0).getFilePath();
//		repairFile = dir + className;
//	}

}
