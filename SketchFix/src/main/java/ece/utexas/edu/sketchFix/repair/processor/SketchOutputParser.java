/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.List;
import java.util.Vector;

import sketch.compiler.ast.core.Program;

public class SketchOutputParser {
	private Vector<String> output = new Vector<String>();
	private int unsat = -1;

	public void append(String line) {
		if (output.contains("/* BEGIN PACKAGE sketchFix*/")) {
			output.clear();
		}
		output.add(line.replace("@sketchFix", ""));
	}

	public List<SkLinePy> parseOutput(Program prog) {
		SkLineMapper lineParser = new SkLineMapper(output);
		lineParser.visitProgram(prog);
		return lineParser.postProcess();
	}

	public List<SkLinePy> parseRepairOutput(Program prog) {
		SkLineMapper lineParser = new SkLineMapper(output);
		lineParser.visitProgram(prog);
		return lineParser.getSkLineList();
	}

	public int parseError(String line) {
		if (line.contains("- ERROR] [SKETCH]")) {
			String[] tokens = line.split(":");
			line = tokens[tokens.length - 1];
			line = line.substring(0, line.indexOf("("));
			return Integer.parseInt(line.trim());
		}
		return -1;
	}

	public Vector<String> getOutput() {
		return output;
	}

	public int getUnsat() {
		return unsat;
	}

	public void setOutput(Vector<String> output) {
		this.output = output;
	}

	public void setUnsat(int unsat) {
		this.unsat = unsat;
	}
	
	
	
}
