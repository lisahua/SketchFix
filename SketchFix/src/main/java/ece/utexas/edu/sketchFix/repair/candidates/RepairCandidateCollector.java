/**
 * @author Lisa Aug 20, 2016 AbstractRepairCandidate.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import ece.utexas.edu.sketchFix.repair.processor.CandidateTemplate;
import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;

public class RepairCandidateCollector {

	SkCandidate generator;

	List<CandidateTemplate> candTemplates = new ArrayList<CandidateTemplate>();
	List<SkCandidate> list = new ArrayList<SkCandidate>();

	public RepairCandidateCollector(SkCandidate generator) {
		this.generator = generator;
		// init templates
		candTemplates.add(new NullExceptionHandler(generator));

		List<SkCandidate> candidates = new ArrayList<SkCandidate>();
		for (CandidateTemplate template : candTemplates)
			candidates.addAll(template.process());
		
		for (SkCandidate cand: candidates) {
			list.add(cand);
		}
	}

	public List<SkCandidate> getCandidates() {
		return list;
	}
}
