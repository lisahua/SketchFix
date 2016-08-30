/**
 * @author Lisa Aug 26, 2016 SkCandidateList.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.stmts.Statement;

public class SkCandidate {

	Program prog;
	List<SkLinePy> beforeRepair;
	String outputFile;
	List<ASTLinePy> states;
	String repairFile;
	MethodData methodData;
	Function currentFunc;
	HashMap<Statement, Integer> stmtScopeMap = new HashMap<Statement, Integer>();

	public SkCandidate(Program prog, List<SkLinePy> lines, List<ASTLinePy> list, MethodData methodData, Function func) {

		this.prog = prog;
		this.beforeRepair = lines;
		this.states = list;
		this.methodData = methodData;
		this.currentFunc = func;

		for (int i = 0; i < beforeRepair.size(); i++) {
			SkLinePy skLine = beforeRepair.get(i);
			if (skLine.getSkStmt() instanceof Function)
				continue;
			stmtScopeMap.put((Statement) skLine.getSkStmt(), i);
		}
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

	// public void setBaseDir(String dir) {
	// String className = "";
	// if (states != null && states.size() > 0)
	// className =
	// states.get(states.size()-1).getLinePyList().get(0).getFilePath();
	// repairFile = dir + className;
	// }

	public List<Statement> getAllTouchStatement() {
		List<Statement> stmtList = new ArrayList<Statement>();
		for (ASTLinePy line : states) {
			stmtList.addAll(line.getSkStmts());
		}
		return stmtList;
	}

	public List<Statement> getAllCurrentMtdTouchStatement() {
		List<Statement> stmtList = new ArrayList<Statement>();
		String currMethod = methodData.getMethodName();
		for (ASTLinePy line : states) {
			if (line.getLinePyList().get(0).getMethodName().equals(currMethod)) {
				stmtList.addAll(line.getSkStmts());
			}
		}
		return stmtList;
	}

	public List<List<Statement>> getAllCurrentFirstTouchStatement() {
		List<List<Statement>> stmtList = new ArrayList<List<Statement>>();
		String currMethod = methodData.getMethodName();
		for (ASTLinePy line : states) {
			if (line.getLinePyList().get(0).getMethodName().equals(currMethod)) {
				stmtList.add(line.getSkStmts());
			}
		}
		return stmtList;
	}

	public Function getCurrentFunc() {
		return currentFunc;
	}

	public void setCurrentFunc(Function currentFunc) {
		this.currentFunc = currentFunc;
	}
	
	public void updateSkLineHole(Statement origin, Statement update, SkLineType lineType) {
		//TODO 
//		scope.get(lastCallID).setSkStmt(block);
//		scope.get(lastCallID).setHole(true);
//		scope.get(lastCallID).setType(SkLineType.STBLOCK);
	}


}
