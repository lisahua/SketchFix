/**
 * @author Lisa Aug 20, 2016 RepairGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.candidates.RepairCandidateCollector;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.TransformResult;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.stmts.Statement;

public class RepairGenerator {
	// Program prog = null;
	// int unsatLineNum = 0;
	private List<SkLinePy> beforeRepair = null;
	TransformResult result;

	public RepairGenerator(TransformResult result) {
		this.result = result;
	}

	public List<SkCandidate> setOutputParser(SketchOutputParser parser) {
		List<SkLinePy> lines = parser.parseOutput(result.getProg());
		lines = processMethodScope(lines, parser.getUnsat(), result.getEditMethod());
//		lines = processTouchScope(lines);
		SkCandidate generator = new SkCandidate(result.getProg(), lines, result.getLines());
		RepairCandidateCollector candidate = new RepairCandidateCollector(generator);
		return candidate.getCandidates();
	}

	

	private List<SkLinePy> processMethodScope(List<SkLinePy> lines, int unsat, String mtdName) {
		int funcID = -1;
		for (int i = 0; i < lines.size(); i++) {
			SkLinePy line = lines.get(i);
			if (line.getSkStmt() instanceof Function) {
				Function func = (Function) line.getSkStmt();
				String fName = func.getName();
				if (fName.contains("_"))
					fName = fName.substring(0, fName.indexOf("_"));
				if (fName.equals(mtdName))
					funcID = i;
				else if (funcID > -1) {
					return lines.subList(funcID, i);
				}
			} else if (lines.get(i).getAssLine() == unsat) {
				return lines.subList(funcID < 0 ? 0 : funcID, i + 1);
			}
		}
		return lines;
	}



	public List<SkLinePy> getScope() {
		return beforeRepair;
	}
}
