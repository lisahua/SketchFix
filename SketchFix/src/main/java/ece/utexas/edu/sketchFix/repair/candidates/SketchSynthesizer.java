/**
 * @author Lisa Aug 19, 2016 SketchSynthesizer.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import sketch.compiler.ast.core.Program;

public class SketchSynthesizer {

	SketchOutputParser parser = new SketchOutputParser();
	RepairGenerator repair = null;

	public SketchSynthesizer (Program prog) {
		repair = new  RepairGenerator(prog);
	}
	
	public void process(String skInput) {
		try {
			Process p = Runtime.getRuntime().exec("sketch " + skInput);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				parser.append(line);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repair.setOutputParser(parser);

	}

}
