/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;
import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.repair.processor.SketchOutputParser;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.Program;

public class SketchRepairValidator {
	SkRepairProcessor repairProcessor;

	public SketchRepairValidator(Program prog, List<ASTLinePy> stateList, List<SkLinePy> beforeRepair) {
		RepairItem repairItem = new RepairItem(prog, stateList, beforeRepair);
		repairProcessor = new SkRepairProcessor(repairItem);
	}

	public SketchRepairValidator(List<SkCandidate> candidates) {
		for (SkCandidate candidate : candidates) {
			process(candidate);
		}
	}

	private void process(SkCandidate candidate) {
		String skInput = candidate.getOutputFile();
		String resultFile = skInput + "_";
		SketchOutputParser parser;
		try {
			if (LocalizerUtility.DEBUG) {
				parser = forTest(resultFile);
			} else {
				parser = invokeCmd(skInput);
			}
			if (parser == null)
				return;
			repairProcessor.setScope(parser.parseRepairOutput(candidate.getProg()));
		} catch (Exception e) {
			if (LocalizerUtility.DEBUG)
				e.printStackTrace();
		}
		// return tranList;
	}

	/**
	 * This is for test purpose
	 * 
	 * @param outputFile
	 * @return
	 */
	private SketchOutputParser forTest(String outputFile) throws Exception {
		SketchOutputParser parser = new SketchOutputParser();
		BufferedReader reader = new BufferedReader(new FileReader(LocalizerUtility.baseDir + outputFile));
		String line = "";
		while ((line = reader.readLine()) != null) {
			parser.append(line);
		}
		reader.close();
		return parser;
	}

	private SketchOutputParser invokeCmd(String skInput) throws Exception {
		PrintWriter writer = new PrintWriter(skInput);
		SketchOutputParser parser = new SketchOutputParser();
		Process p = Runtime.getRuntime().exec("sketch " + skInput);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			parser.append(line);
			writer.println(line);
		}
		reader.close();
		writer.close();

		reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String firstLine = null;
		while ((line = reader.readLine()) != null) {
			if (firstLine == null) {
				firstLine = line;
				System.out.println("[Step 3: Sketch Synthesizer] " + line);
			}
		}
		if (firstLine != null)
			return null;
		return parser;
	}

	private void writeFile(Program prog, String outputFile) {
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
