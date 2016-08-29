/**
 * @author Lisa Aug 26, 2016 TransformResult.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;

public class TransformResult {

	Program prog;
	List<ASTLinePy> lines;
	String outputFile;
	Function currentFunc;
MethodData data;
	public TransformResult(Program prog, List<ASTLinePy> lines, String outputFile, Function func,MethodData data) {
		this.prog = prog;
		this.lines = lines;
		this.outputFile = outputFile;
		currentFunc = func;
		this.data = data;
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

	public String getEditMethod() {
		return currentFunc.getName();
	}

	public MethodData getData() {
		return data;
	}

	public void setData(MethodData data) {
		this.data = data;
	}

}
