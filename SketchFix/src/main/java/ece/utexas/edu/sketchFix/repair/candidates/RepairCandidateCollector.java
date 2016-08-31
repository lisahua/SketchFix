/**
 * @author Lisa Aug 20, 2016 AbstractRepairCandidate.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.CandidateTemplate;
import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;

public class RepairCandidateCollector {

	SkCandidate generator;
	List<SkCandidate> candidates = new ArrayList<SkCandidate>();
	List<CandidateTemplate> candTemplates = new ArrayList<CandidateTemplate>();

	public RepairCandidateCollector(SkCandidate generator) {
		this.generator = generator;
		// init templates
		candTemplates.add(new NullExceptionHandler(generator));

		
		
		for (CandidateTemplate template : candTemplates)
			candidates.addAll(template.process());
	}

	public List<SkCandidate> getCandidates() {
		return candidates;
	}
}
