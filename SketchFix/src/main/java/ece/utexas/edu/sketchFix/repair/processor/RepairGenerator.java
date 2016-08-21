/**
 * @author Lisa Aug 20, 2016 RepairGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.List;

import ece.utexas.edu.sketchFix.repair.candidates.AbstractRepairCandidate;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;

public class RepairGenerator {
	Program prog = null;
	int unsatLineNum = 0;
	private List<SkLinePy> beforeRepair = null;
	public RepairGenerator(Program prog) {
		this.prog = prog;
	}

	public Program setOutputParser(SketchOutputParser parser) {
		List<SkLinePy> lines = parser.parseOutput(prog);
		SkCandidateGenerator generator = new SkCandidateGenerator(prog);
		AbstractRepairCandidate candidate = new AbstractRepairCandidate(generator);
	Program prog =  (Program) candidate.setScope(process(lines));
	beforeRepair = candidate.getScope();
	return prog;

	}

	private List<SkLinePy> process(List<SkLinePy> lines) {
		int funcID = 0;
		for (int i = 0; i < lines.size(); i++) {
			SkLinePy line = lines.get(i);
			if (line.getSkStmt() instanceof Function)
				funcID = i;
			else if (lines.get(i).getAssLine() == unsatLineNum) {
				return lines.subList(funcID, i + 1);
			}
		}
		return lines;
	}

	public void setUnSatLineNum(int unsat) {
		unsatLineNum = unsat;

	}

	public List<SkLinePy> getScope() {
		return beforeRepair;
	}
}
