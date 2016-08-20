/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.TreeMap;
import java.util.Vector;

import sketch.compiler.ast.core.Program;

public class SketchOutputParser {
	private Vector<String> output = new Vector<String>();
	private int unsat = -1;

	public void append(String line) {
		if (output.contains(line)) {
			output.remove(line);
		}
		output.add(line);
	}

	public void parseOutput(Program prog) {
		TreeMap<Integer, Integer> assList = new TreeMap<Integer, Integer>();
		for (int i = 0; i < output.size(); i++) {
			String line = output.get(i);
			int isAssert = isAssert(line);
			int isOutput = isOutput(line);
			if (isAssert > 0)
				assList.put(isAssert, i);
			else if (isOutput > 0)
				unsat = isOutput;
		}
	}

	private int isAssert(String line) {
		if (line.contains("//Assert at ")) {
			String[] tokens = line.split(":");
			line = tokens[tokens.length - 1];
			line = line.substring(0, line.indexOf("("));
			return Integer.parseInt(line.trim());
		}
		return -1;
	}

	private int isOutput(String line) {
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
