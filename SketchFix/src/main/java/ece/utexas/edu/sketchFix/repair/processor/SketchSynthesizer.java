/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import ece.utexas.edu.sketchFix.staticTransform.TransformResult;
import sketch.compiler.ast.core.Program;

public class SketchSynthesizer {

	List<SkCandidate> candList = new ArrayList<SkCandidate>();

	public SketchSynthesizer(List<TransformResult> suspLoc) {
		for (TransformResult location : suspLoc) {
			RepairGenerator repair = new RepairGenerator(location.getProg());
			candList = process(location.getOutputFile(), repair);
		}
	}

	private List<SkCandidate> process(String skInput, RepairGenerator repair) {
		String resultFile = skInput + "_";
		try {
			SketchOutputParser parser;
			if (LocalizerUtility.DEBUG) {
				parser = forTest(resultFile);
			} else {
				parser = invokeCmd(skInput);
			}
			List<SkCandidate> candidates = repair.setOutputParser(parser);
			for (int i = 0; i < candidates.size(); i++) {
				candidates.get(i).setOutputFile(resultFile + i);
			}
			return candList;
		} catch (Exception e) {
			if (LocalizerUtility.DEBUG)
				e.printStackTrace();
		}
		return new ArrayList<SkCandidate>();

	}

	private SketchOutputParser invokeCmd(String skInput) throws Exception {
		String resultFile = skInput + "_";
		PrintWriter writer = new PrintWriter(resultFile);
		Process p = Runtime.getRuntime().exec("sketch " + skInput);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		SketchOutputParser parser = new SketchOutputParser();
		String line = "";
		while ((line = reader.readLine()) != null) {
			parser.append(line);
			writer.println(line);
		}

		reader.close();
		writer.close();
		int unsat = -1;
		reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String firstLine = null;
		while ((line = reader.readLine()) != null) {
			int i = parser.parseError(line);
			unsat = Math.max(i, unsat);
			if (firstLine == null) {
				firstLine = line;
				System.out.println("[Step 3: Sketch Synthesizer] " + line);
			}
		}
		if (firstLine == null) {
			System.out.println("[Step 3: Sketch Synthesizer] No error");
		}
		parser.setUnsat(unsat);

		return parser;
	}

	/**
	 * 
	 * This is for test purpose
	 * 
	 * @param outputFile
	 */
	public SketchOutputParser forTest(String outputFile) throws Exception {
		BufferedReader reader;
		SketchOutputParser parser = new SketchOutputParser();
		reader = new BufferedReader(new FileReader(outputFile));
		// reader = new BufferedReader(new FileReader(LocalizerUtility.baseDir +
		// outputFile));
		String line = "";
		while ((line = reader.readLine()) != null) {
			parser.append(line);
		}
		reader.close();
		parser.setUnsat(85);

		return parser;
	}

	private void writeFile(Program prog, String outputFile) {
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<SkCandidate> getCandidateList() {
		return candList;
	}
}
