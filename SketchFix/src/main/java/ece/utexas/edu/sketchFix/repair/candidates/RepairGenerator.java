/**
 * @author Lisa Aug 20, 2016 RepairGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import sketch.compiler.ast.core.Program;

public class RepairGenerator {
	Program prog = null;

	public RepairGenerator(Program prog) {
		this.prog = prog;
	}

	public void setOutputParser(SketchOutputParser parser) {
		parser.parseOutput(prog);
		if (parser.getUnsat() <= 0)
			return;
		
//		NullExceptionHandler handler = new NullExceptionHandler(parser.getSuspLine());
//		handler.visitProgram(prog);
		
		process();
	}

	private void process() {

	}

}
