/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.Program;

public class SketchSynthesizer {

	SketchOutputParser parser = new SketchOutputParser();
	RepairGenerator repair = null;

	public SketchSynthesizer(Program prog) {
		repair = new RepairGenerator(prog);
	}

	public void process(String skInput) {
		String resultFile = skInput.replace("2", "3");
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
			while ((line = reader.readLine()) != null) {
				int i = parser.parseError(line);
				unsat = Math.max(i, unsat);
			}
			repair.setUnSatLineNum(unsat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Program prog = repair.setOutputParser(parser);
		writeFile(prog, skInput);

	}

	/**
	 * This is for test purpose
	 * 
	 * @param outputFile
	 */
	public void forTest(String outputFile) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(outputFile));

			String line = "";
			while ((line = reader.readLine()) != null) {
				parser.append(line);
			}
			repair.setUnSatLineNum(68);
			Program prog = repair.setOutputParser(parser);
			writeFile(prog, outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
private void writeFile(Program prog, String outputFile) {
	try {
		prog.accept(new SimpleSketchFilePrinter(outputFile ));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
}
}