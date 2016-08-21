/**
 * @author Lisa Aug 20, 2016 AbstractRepairCandidate.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkCandidateGenerator;
import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import sketch.compiler.ast.core.Program;

public class AbstractRepairCandidate  {

	protected SkCandidateGenerator candGenerator = null;
	protected List<SkLinePy> scope;

	public AbstractRepairCandidate() {

	}

	public AbstractRepairCandidate(SkCandidateGenerator generator) {
		candGenerator = generator;
	}

	public Object visitProgram(Program prog) {
		NullExceptionHandler handler = new NullExceptionHandler(this);
		return handler.visitProgram(prog);
	}

	public Object setScope(List<SkLinePy> scope) {
		this.scope = scope;
		return visitProgram(candGenerator.getProgram());
	}
}
