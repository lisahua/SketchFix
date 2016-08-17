/**
 * @author Lisa Aug 16, 2016 StateReverter.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.io.FileNotFoundException;

import ece.utexas.edu.sketchFix.staticTransform.AbstractSketchTransformer;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.Program;

public class StateReverter {
	Program prog = null;

	public StateReverter(AbstractSketchTransformer assertTran, AbstractSketchTransformer sourceTran) {
		TransHandler combinator = new TransformerCombinator(assertTran, sourceTran);
		TransHandler overloader = new TransOverloadHandler(combinator, assertTran, sourceTran);
		Program prog = overloader.generateProg();
		InheritanceReplacer inheritReplacer = new InheritanceReplacer();
		prog = (Program) inheritReplacer.visitProgram(prog);

	}

	public void writeToFile(String outputFile) {
		if (prog == null)
			return;
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
