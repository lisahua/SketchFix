/**
 * @author Lisa Aug 20, 2016 SkCandidateGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.ArrayList;
import java.util.List;

import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Program;

public class CandidateTemplate extends FEReplacer {

	protected List<SkLinePy> scope;
	protected Program prog;
	protected List<SkCandidate> list = new ArrayList<SkCandidate>();

	public CandidateTemplate(SkCandidate generator) {
		this.prog = generator.prog;
		scope = generator.beforeRepair;
	}

	public List<SkLinePy> getScope() {
		return scope;
	}

	public Program getProg() {
		return prog;
	}

	public List<SkCandidate> getCandidates() {
		return list;
	}

}
