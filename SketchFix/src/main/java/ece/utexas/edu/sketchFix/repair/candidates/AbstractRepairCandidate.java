/**
 * @author Lisa Aug 20, 2016 AbstractRepairCandidate.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import sketch.compiler.ast.core.FEReplacer;

public class AbstractRepairCandidate extends FEReplacer {

	protected SkCandidateGenerator candGenerator = null;

	public void setCandGenerator(SkCandidateGenerator candGenerator) {
		this.candGenerator = candGenerator;
	}
}
