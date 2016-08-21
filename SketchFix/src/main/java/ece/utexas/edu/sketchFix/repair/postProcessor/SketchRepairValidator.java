/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SketchOutputParser;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Program;

public class SketchRepairValidator {
	SkRepairProcessor repairProcessor;
	SketchOutputParser parser = new SketchOutputParser();
	Program prog;

	public SketchRepairValidator(Program prog, List<ASTLinePy> assList, List<ASTLinePy> codeList, FENode feNode) {
		this.prog = prog;
		repairProcessor = new SkRepairProcessor(assList, codeList,feNode);
	}

	public void process(String skInput) {
		String resultFile = skInput.replace("3", "4");
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
			repairProcessor.setScope(parser.parseRepairOutput(prog));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is for test purpose
	 * 
	 * @param outputFile
	 */
	// public void forTest(String outputFile) {
	// BufferedReader reader;
	// try {
	// reader = new BufferedReader(new FileReader(outputFile));
	//
	// String line = "";
	// while ((line = reader.readLine()) != null) {
	// parser.append(line);
	// }
	// repair.setUnSatLineNum(68);
	// Program prog = repair.setOutputParser(parser);
	// writeFile(prog, outputFile);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	private void writeFile(Program prog, String outputFile) {
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
