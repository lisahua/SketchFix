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
		output.add(line);
	}

	public List<SkLinePy> parseOutput(Program prog) {
		SkLineMapper lineParser = new SkLineMapper(output);
		lineParser.visitProgram(prog);
		return lineParser.postProcess();
		
		
	}

//	private int isAssert(String line) {
//		if (line.contains("//Assert at ")) {
//			String[] tokens = line.split(":");
//			line = tokens[tokens.length - 1];
//			line = line.substring(0, line.indexOf("("));
//			return Integer.parseInt(line.trim());
//		}
//		return -1;
//	}

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

	public String getSuspLine() {
		//FIXME buggy
		return output.get(unsat - 1);
	}
}