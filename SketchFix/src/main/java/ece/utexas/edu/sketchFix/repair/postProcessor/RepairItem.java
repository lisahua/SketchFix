/**
 * @author Lisa Aug 26, 2016 RepairResult.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;
import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.Program;

public class RepairItem {
	Program prog;
	List<ASTLinePy> stateList;
	List<SkLinePy> beforeRepair;

	public RepairItem(SkCandidate candidate) {
		this.prog = candidate.getProg();
		stateList = candidate.getStates();
		this.beforeRepair = candidate.getBeforeRepair();
	}

	public Program getProg() {
		return prog;
	}

	public void setProg(Program prog) {
		this.prog = prog;
	}

	public List<ASTLinePy> getStateList() {
		return stateList;
	}

	public void setStateList(List<ASTLinePy> stateList) {
		this.stateList = stateList;
	}

	public List<SkLinePy> getBeforeRepair() {
		return beforeRepair;
	}

	public void setBeforeRepair(List<SkLinePy> beforeRepair) {
		this.beforeRepair = beforeRepair;
	}

}
