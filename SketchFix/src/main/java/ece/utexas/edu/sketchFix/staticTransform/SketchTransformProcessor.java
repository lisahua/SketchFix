/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.repair.Argument;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.stateRevert.StateInsertProcessor;
import ece.utexas.edu.sketchFix.stateRevert.TransformPostProcessor;
import sketch.compiler.ast.core.Program;

public class SketchTransformProcessor {
	private Argument arg = null;
	// private Vector<LinePy> trace;
//	List<ASTLinePy> lines = new ArrayList<ASTLinePy>();
//	Program prog;
	List<TransformResult> suspLocations = new ArrayList<TransformResult>();

	public SketchTransformProcessor(Argument argument) {
		arg = argument;
	}

	public void process(LinePyGenerator generator, List<MethodData> locations, MethodData testMethod,
			String outputFile) {
		AbstractSketchTransformer assertTran = new SketchAssertTransformer(testMethod);

		testMethod.setBasrDirs(arg.getSourceDir());
		testMethod.setBaseDir(arg.getSourceDir()[1]);
		// transform sketch assertion

		try {
			assertTran.transform(testMethod, generator, locations);

			for (int i = 0; i < LocalizerUtility.MAX_REPAIR_LOC; i++) {
				List<ASTLinePy> lines = new ArrayList<ASTLinePy>();
				AbstractSketchTransformer sourceTran = new SketchSourceTransformer();
				MethodData data = locations.get(0);
				data.setBaseDir(arg.getSourceDir()[0]);
				data.setBasrDirs(arg.getSourceDir());
				sourceTran.setRefTransformer(assertTran);
				sourceTran.transform(locations.get(0), generator, locations);
				// StmtStateMapper sourceState = sourceTran.getStateMapper();

				TransformPostProcessor reverter = new TransformPostProcessor(sourceTran);
				// reverter.writeToFile(outputFile);
				Program prog = reverter.getProgram();
				StateInsertProcessor replacer = new StateInsertProcessor(assertTran.getStateMapper().getLinePyList(),
						sourceTran.getStateMapper().getLinePyList());
				lines = replacer.getAllLines();
				prog = (Program) replacer.visitProgram(prog);
				if (prog == null)
					continue;
				try {
					prog.accept(new SimpleSketchFilePrinter(outputFile+"i"));
					suspLocations.add(new TransformResult(prog, lines, outputFile+"i"));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			if (LocalizerUtility.DEBUG)
				e.printStackTrace();
		}

	}

	// public List<ASTLinePy> getLines() {
	// return lines;
	// }
	//
	// public Program getProg() {
	// return prog;
	// }

	public List<TransformResult> getSuspLocations() {
		return suspLocations;
	}

}