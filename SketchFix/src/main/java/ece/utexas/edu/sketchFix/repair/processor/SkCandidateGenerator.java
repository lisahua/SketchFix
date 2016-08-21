/**
 * @author Lisa Aug 20, 2016 SkCandidateGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.exprs.ExprStar;
import sketch.compiler.ast.core.exprs.Expression;

public class SkCandidateGenerator {
	Program prog;

	public SkCandidateGenerator(Program prog) {
		this.prog = prog;
	}

	/**
	 * FIXME for scope line
	 * 
	 * @param mtd
	 * @param type
	 */
	public Expression getStmtAssign(String mtd, String type) {
		
		return new ExprStar(prog.getOrigin());
	}

	public Program getProgram() {
		return prog;
	}
}
