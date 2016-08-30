/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.repair.Argument;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.stateRevert.StateInsertProcessor;
import ece.utexas.edu.sketchFix.stateRevert.TransformPostProcessor;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.stmts.StmtBlock;

public class SketchTransformProcessor {
	private Argument arg = null;
	// private Vector<LinePy> trace;
	// List<ASTLinePy> lines = new ArrayList<ASTLinePy>();
	// Program prog;
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
			StateInsertProcessor replacer = new StateInsertProcessor();
			assertTran.transform(testMethod, generator, locations);
			replacer.insertStates(assertTran.getStateMapper().getLinePyList());
			replacer.setTestMtd(assertTran.getCurrMethod());
			// List<ASTLinePy> states = new ArrayList<ASTLinePy>();
			for (int i = 0, index = 0; index < LocalizerUtility.MAX_REPAIR_LOC
					&& i < LocalizerUtility.MAX_METHOD_THRESHOLD; i++) {
				List<ASTLinePy> lines = new ArrayList<ASTLinePy>();
				AbstractSketchTransformer sourceTran = new SketchSourceTransformer();
				MethodData data = locations.get(i);
				if (data.getTouchLinesList().size() < 2)
					continue;
				data.setBaseDir(arg.getSourceDir()[0]);
				data.setBasrDirs(arg.getSourceDir());
				sourceTran.setRefTransformer(assertTran);
				sourceTran.transform(data, generator, locations);
				// StmtStateMapper sourceState = sourceTran.getStateMapper();
				if (sourceTran.getCurrMethod() == null
						|| ((StmtBlock) sourceTran.getCurrMethod().getBody()).getStmts().size() < 2)
					continue;

				TransformPostProcessor reverter = new TransformPostProcessor(sourceTran);
				// reverter.writeToFile(outputFile);
				Program prog = reverter.getProgram();
				if (sourceTran.getStateMapper() == null)
					continue;
				replacer.insertStates(sourceTran.getStateMapper().getLinePyList());
				replacer.setCurrentMtd(sourceTran.getCurrMethod());
				lines = replacer.getAllLines();
				prog = (Program) replacer.visitProgram(prog);
				if (prog == null)
					continue;
				index++;
				// prog.accept(new SimpleSketchFilePrinter(outputFile + index));
				suspLocations.add(new TransformResult(prog, lines, outputFile + index, sourceTran.getCurrMethod(), data, replacer.getInvariantMap()));
				// System.out.println("[Step 1: Checking suspicious
				// location:]"+(outputFile + index) +":"+
				// data.getClassFullPath() + ":"
				// + data.getMethodNameWithParam()
				// );

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