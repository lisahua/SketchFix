/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
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
	SketchOutputParser parser = new SketchOutputParser();
	Program prog;
	List<RepairTransformer> tranList = new ArrayList<RepairTransformer>();

	public SketchRepairValidator(Program prog, List<ASTLinePy> assList, List<SkLinePy> beforeRepair) {
		this.prog = prog;
		repairProcessor = new SkRepairProcessor(assList, beforeRepair);
	}

	public SketchRepairValidator(List<SkCandidate> candidates) {

	}

	public List<RepairResult> process(String skInput) {
		String resultFile = skInput + "_";
		if (LocalizerUtility.DEBUG) {
			forTest(resultFile);
			// return tranList;
			// return repairProcessor.setScope(parser.parseRepairOutput(prog));
		}
		try {
			PrintWriter writer = new PrintWriter(resultFile);
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
			RepairTransformer repairTrans = repairProcessor.setScope(parser.parseRepairOutput(prog));
			// return repairTrans;
			// return tranList;
		} catch (Exception e) {
			if (LocalizerUtility.DEBUG)
				e.printStackTrace();
		}
		return null;
		// return tranList;
	}

	/**
	 * This is for test purpose
	 * 
	 * @param outputFile
	 * @return
	 */
	public RepairTransformer forTest(String outputFile) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(LocalizerUtility.baseDir + outputFile));
			String line = "";
			while ((line = reader.readLine()) != null) {
				parser.append(line);
			}
			reader.close();
			return repairProcessor.setScope(parser.parseRepairOutput(prog));
			// writeFile(prog, outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void writeFile(Program prog, String outputFile) {
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
