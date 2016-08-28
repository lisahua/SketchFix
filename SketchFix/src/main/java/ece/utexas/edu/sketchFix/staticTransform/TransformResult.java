/**
 * @author Lisa Aug 26, 2016 TransformResult.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import sketch.compiler.ast.core.Program;

public class TransformResult {

	Program prog;
	List<ASTLinePy> lines;
	String outputFile;

	public TransformResult(Program prog, List<ASTLinePy> lines, String outputFile) {
		this.prog = prog;
		this.lines = lines;
		this.outputFile = outputFile;
	}

	public Program getProg() {
		return prog;
	}

	public List<ASTLinePy> getLines() {
		return lines;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

}
