/**
 * @author Lisa Aug 20, 2016 AbstractRepairCandidate.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;
import ece.utexas.edu.sketchFix.repair.processor.CandidateTemplate;
import sketch.compiler.ast.core.Program;

public class RepairCandidateCollector {

	SkCandidate generator;
	List<SkCandidate> candidates = new ArrayList<SkCandidate>();
	List<CandidateTemplate> candTemplates = new ArrayList<CandidateTemplate>();

	public RepairCandidateCollector(SkCandidate generator) {
		this.generator = generator;
		// init templates
		candTemplates.add(new NullExceptionHandler(generator));
		
		
		
		int outputFileCount = 0;
		// execute each repair templates
		Program prog = generator.getProg();
		for (CandidateTemplate template : candTemplates) {
			Program updateProg = (Program) template.visitProgram(prog);
			int verified = -1;
			// FIXME
			while (verified != 0) {
				verified = template.validateWithSketch(updateProg);
				if (verified == 1) {
					SkCandidate cand = new SkCandidate(updateProg, template.getScope(), generator.getStates(),
							generator.getMethodData(), generator.getCurrentFunc());
					cand.setOutputFile(generator.getOutputFile() + outputFileCount++);
					candidates.add(cand);
				}
			}
		}
	}

	public List<SkCandidate> getCandidates() {
		return candidates;
	}
}
