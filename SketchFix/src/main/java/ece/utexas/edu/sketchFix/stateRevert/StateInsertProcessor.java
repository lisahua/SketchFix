/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Program;

public class StateInsertProcessor extends FEReplacer {

	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();

	public StateInsertProcessor(List<ASTLinePy> assLines, List<ASTLinePy> codeLines) {
		allLines.addAll(assLines);
		allLines.addAll(codeLines);
	}

	public Object visitProgram(Program prog) {
		//FIXME more replacer goes here
		TraceConnectionReplacer traceConnect = new TraceConnectionReplacer (allLines);
		prog = (Program) traceConnect.visitProgram(prog);
		
		NotNullTraceReplacer replacer = new NotNullTraceReplacer(allLines);
		prog = (Program) replacer.visitProgram(prog);
		
		
		return prog;
	}

	public List<ASTLinePy> getAllLines() {
		return allLines;
	}

}
