/**
 * @author Lisa Aug 20, 2016 RepairGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.List;

import ece.utexas.edu.sketchFix.repair.candidates.RepairCandidateCollector;
import ece.utexas.edu.sketchFix.staticTransform.TransformResult;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;

public class RepairGenerator {
//	Program prog = null;
//	int unsatLineNum = 0;
	private List<SkLinePy> beforeRepair = null;
	TransformResult result;
	public RepairGenerator(TransformResult result) {
		this.result = result;
	}

	public List<SkCandidate> setOutputParser(SketchOutputParser parser) {
		List<SkLinePy> lines = parser.parseOutput(result.getProg());
		lines = process(lines, parser.getUnsat());
		SkCandidate generator = new SkCandidate(result.getProg(),  lines, result.getLines());
		RepairCandidateCollector candidate = new RepairCandidateCollector(generator);
		return candidate.getCandidates();
	}

	private List<SkLinePy> process(List<SkLinePy> lines, int unsat) {
		int funcID = 0;
		for (int i = 0; i < lines.size(); i++) {
			SkLinePy line = lines.get(i);
			if (line.getSkStmt() instanceof Function)
				funcID = i;
			else if (lines.get(i).getAssLine() == unsat) {
				return lines.subList(funcID, i + 1);
			}
		}
		return lines;
	}

	
	public List<SkLinePy> getScope() {
		return beforeRepair;
	}
}
