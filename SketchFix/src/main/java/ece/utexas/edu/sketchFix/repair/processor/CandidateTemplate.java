/**
 * @author Lisa Aug 20, 2016 SkCandidateGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;

public abstract class CandidateTemplate extends FEReplacer {

	protected List<SkLinePy> scope;
	protected SkCandidate originCand = null;
	protected int locCount = 0;
	protected Function currentFunc;

	public CandidateTemplate(SkCandidate generator) {
		// this.prog = generator.prog;
		scope = generator.beforeRepair;
		originCand = generator;
		currentFunc = generator.getCurrentFunc();
	}

	public List<SkLinePy> getScope() {
		return scope;
	}

	// public Program getProg() {
	// return prog;
	// }

	public int validateWithSketch(Program updateProg) {
		Process p;
		try {
			// FIXME
			updateProg.accept(new SimpleSketchFilePrinter(originCand.getOutputFile()));
			System.out.println("[Generate Repair] " + originCand.getOutputFile());
			if (LocalizerUtility.DEBUG) {
				return 0;
			} else {
				p = Runtime.getRuntime().exec("sketch " + originCand.getOutputFile());
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					// FIXME buggy
					if (hasDone())
						return 0;
					else if (line.length() > 0) {
						locCount++;
						return -1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	protected abstract boolean hasDone();
}
