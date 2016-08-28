/**
 * @author Lisa Aug 26, 2016 SkCandidateList.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.List;

import sketch.compiler.ast.core.Program;

public class SkCandidate {

	SketchOutputParser parser ;
	Program prog ;
	List<SkLinePy> beforeRepair;
	String outputFile;
	
	public SkCandidate(Program prog , SketchOutputParser parser ,List<SkLinePy> beforeRepair) {
		this.parser = parser;
		this.prog = prog;
		this.beforeRepair = beforeRepair;
		
	}
	
	public SketchOutputParser getParser() {
		return parser;
	}
	public void setParser(SketchOutputParser parser) {
		this.parser = parser;
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
	
	
	
}
