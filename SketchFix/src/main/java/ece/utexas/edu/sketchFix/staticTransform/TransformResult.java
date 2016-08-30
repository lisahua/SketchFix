/**
 * @author Lisa Aug 26, 2016 TransformResult.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.HashMap;
import java.util.List;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.exprs.Expression;

public class TransformResult {

	Program prog;
	List<ASTLinePy> lines;
	String outputFile;
	Function currentFunc;
	MethodData data;
	HashMap<Expression, Integer> invariantMap;

	public TransformResult(Program prog, List<ASTLinePy> lines, String outputFile, Function func, MethodData data,
			HashMap<Expression, Integer> invariantMap) {
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

	public Function getCurrentFunc() {
		return currentFunc;
	}

	public void setCurrentFunc(Function currentFunc) {
		this.currentFunc = currentFunc;
	}

	public HashMap<Expression, Integer> getInvariantMap() {
		return invariantMap;
	}

	public void setInvariantMap(HashMap<Expression, Integer> invariantMap) {
		this.invariantMap = invariantMap;
	}

}
