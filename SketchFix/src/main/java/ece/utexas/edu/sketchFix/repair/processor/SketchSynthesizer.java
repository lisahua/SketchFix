/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.Program;

public class SketchSynthesizer {

	SketchOutputParser parser = new SketchOutputParser();
	RepairGenerator repair = null;

	public SketchSynthesizer(Program prog) {
		repair = new RepairGenerator(prog);
	}

	public Program process(String skInput) {
		String resultFile = skInput + "_";
		if (LocalizerUtility.DEBUG) {
			forTest(resultFile);
			return repair.setOutputParser(parser);
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
			int unsat = 0;
			reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String firstLine = null;
			while ((line = reader.readLine()) != null) {
				int i = parser.parseError(line);
				unsat = Math.max(i, unsat);
				if (firstLine == null) {
					firstLine = line;
					System.out.println("[Sketch Synthesizer] " + line);
				}
			}
			repair.setUnSatLineNum(unsat);
		} catch (Exception e) {
			if (LocalizerUtility.DEBUG)
				e.printStackTrace();
		}
		Program prog = repair.setOutputParser(parser);
		if (prog != null)
			writeFile(prog, resultFile + "_");
		if (repair.unsatLineNum <= 0)
			return null;
		return prog;
	}

	/**
	 * 
	 * This is for test purpose
	 * 
	 * @param outputFile
	 */
	public Program forTest(String outputFile) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(LocalizerUtility.baseDir + outputFile));
			String line = "";
			while ((line = reader.readLine()) != null) {
				parser.append(line);
			}
			// repair.setUnSatLineNum(68);

			// writeFile(prog, outputFile.substring(0, outputFile.length() - 1)
			// + "4");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return repair.setOutputParser(parser);
	}

	private void writeFile(Program prog, String outputFile) {
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<SkLinePy> getScope() {

		return repair.getScope();
	}

}
