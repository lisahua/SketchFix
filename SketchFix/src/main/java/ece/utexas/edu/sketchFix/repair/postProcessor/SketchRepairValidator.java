/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.repair.processor.SketchOutputParser;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.Program;
import sketch.compiler.main.seq.SequentialSketchMain;
import sketch.compiler.main.seq.SequentialSketchMain.SynthesisResult;

public class SketchRepairValidator {
	SkRepairProcessor repairProcessor;
	SketchOutputParser parser = new SketchOutputParser();
	Program prog;

	public SketchRepairValidator(Program prog, List<ASTLinePy> assList, List<ASTLinePy> codeList,
			List<SkLinePy> beforeRepair) {
		this.prog = prog;
		repairProcessor = new SkRepairProcessor(assList, codeList, beforeRepair);
	}

	public RepairTransformer process(String skInput) {
		String resultFile = skInput.substring(0, skInput.length() - 1) + "5";
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
			return repairProcessor.setScope(parser.parseRepairOutput(prog));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Deprecated
	// FIXME unknown exception
	private void runSketchMain(Program prog, String output) {
		String[] arg = new String[1];
		arg[0] = output;
		SequentialSketchMain mainRunner = new SequentialSketchMain(arg);
		SynthesisResult synthResult = mainRunner.partialEvalAndSolve(prog);
		prog = synthResult.lowered.result;
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
			reader = new BufferedReader(new FileReader(outputFile));
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
