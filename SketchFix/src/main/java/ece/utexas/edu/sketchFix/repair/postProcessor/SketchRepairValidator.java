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
import ece.utexas.edu.sketchFix.repair.processor.SketchOutputParser;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.stmts.Statement;

public class SketchRepairValidator {

	public SketchRepairValidator(List<SkCandidate> candidates) {
		for (SkCandidate candidate : candidates) {
			process(candidate);
		}
	}

	private void process(SkCandidate candidate) {
		String skInput = candidate.getOutputFile();
		String resultFile = skInput + "_";
		try {
			List<Statement> touchLines = new ArrayList<Statement>();
			for (ASTLinePy line : candidate.getStates()) {
				touchLines.addAll(line.getSkStmts());
			}
			SketchOutputParser parser = new SketchOutputParser(touchLines);
			
			candidate.getProg().accept(new SimpleSketchFilePrinter(candidate.getOutputFile()));
			System.out.println("[Step 3: Sketch Synthesizer] Generate repair candidate " + candidate.getOutputFile());
			
			if (LocalizerUtility.DEBUG) {
				parser = forTest(parser, resultFile);
			} else {
				parser = invokeCmd(parser, candidate.getOutputFile());
			}
			if (parser == null)
				return;
			SketchRepairDeltaMapper mapper = new SketchRepairDeltaMapper(new RepairItem(candidate));
			SketchToDOMTransformer transformer = mapper.setNewScope(parser.parseRepairOutput(candidate.getProg()));
			SketchRewriterProcessor rewriter = new SketchRewriterProcessor(candidate.getFileAbsolutePath());
			rewriter.process(transformer);
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
	private SketchOutputParser forTest(SketchOutputParser parser, String outputFile) throws Exception {
		// SketchOutputParser parser = new SketchOutputParser();
		BufferedReader reader = new BufferedReader(new FileReader(outputFile));
		// BufferedReader reader = new BufferedReader(new
		// FileReader(LocalizerUtility.baseDir + outputFile));
		String line = "";
		while ((line = reader.readLine()) != null) {
			parser.append(line);
		}
		reader.close();

		return parser;
	}

	private SketchOutputParser invokeCmd(SketchOutputParser parser, String skInput) throws Exception {
		String resultFile = skInput + "_";
		PrintWriter writer = new PrintWriter(resultFile);
		// SketchOutputParser parser = new SketchOutputParser();
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
				// System.out.println("[Step 3: Sketch Synthesizer] " + line);
			}
		}
		// if (firstLine != null)
		// return null;
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
