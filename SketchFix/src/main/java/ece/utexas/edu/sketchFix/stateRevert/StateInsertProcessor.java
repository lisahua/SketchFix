/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.exprs.Expression;

public class StateInsertProcessor extends FEReplacer {

	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();
	Function currentMtd;
	Function testMethod;

	HashMap<Expression, Integer> invariantMap = new HashMap<Expression, Integer>();
	HashMap<Expression, Integer> invViolation = new HashMap<Expression, Integer>();

	public StateInsertProcessor() {
	}

	public Object visitProgram(Program prog) {
		// FIXME more replacer goes here
		TraceConnectionReplacer traceConnect = new TraceConnectionReplacer(allLines, currentMtd);
		prog = (Program) traceConnect.visitProgram(prog);

		NotNullTraceReplacer replacer = new NotNullTraceReplacer(allLines, currentMtd);
		prog = (Program) replacer.visitProgram(prog);
		ConditionTraceReplacer condRep = new ConditionTraceReplacer(allLines, currentMtd);
		prog = (Program) condRep.visitProgram(prog);

		invariantMap.putAll(replacer.getTraceInvariant());
		invariantMap.putAll(replacer.getViolation());
		invariantMap.putAll(condRep.getTraceInvariant());
		return prog;
	}

	public List<ASTLinePy> getAllLines() {
		return allLines;
	}

	public void insertStates(List<ASTLinePy> linePyList) {
		allLines.addAll(linePyList);
	}

	public void setCurrentMtd(Function currentFunc) {
		this.currentMtd = currentFunc;
	}

	public void setTestMtd(Function testMtd) {
		this.testMethod = testMtd;
	}

	public HashMap<Expression, Integer> getInvariantMap() {
		return invariantMap;
	}
}
