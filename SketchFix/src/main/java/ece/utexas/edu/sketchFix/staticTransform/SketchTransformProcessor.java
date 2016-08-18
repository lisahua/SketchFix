/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.repair.Argument;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.stateRevert.TransformPostProcessor;

public class SketchTransformProcessor {
	private Argument arg = null;
//	private Vector<LinePy> trace;

	public SketchTransformProcessor(Argument argument) {
		arg = argument;
	}

	public void process(LinePyGenerator generator, List<MethodData> locations, MethodData testMethod,
			String outputFile) {
		AbstractSketchTransformer assertTran = new SketchAssertTransformer(testMethod);
		// FIXME buggy
		testMethod.setBasrDirs(arg.getSourceDir());
		testMethod.setBaseDir(arg.getSourceDir()[1]);
		// transform sketch assertion
		assertTran.transform(testMethod, generator, locations);
		// StmtStateMapper assState = assertTran.getStateMapper();

		AbstractSketchTransformer sourceTran = new SketchSourceTransformer();
		MethodData data = locations.get(0);
		data.setBaseDir(arg.getSourceDir()[0]);
		data.setBasrDirs(arg.getSourceDir());
		sourceTran.setRefTransformer(assertTran);
		sourceTran.transform(locations.get(0), generator, locations);
		// StmtStateMapper sourceState = sourceTran.getStateMapper();

		TransformPostProcessor reverter = new TransformPostProcessor(sourceTran);
		reverter.writeToFile(outputFile);

		// assertTran.mergeAnotherTransformer(sourceTran);
		// assertTran.writeToFile(outputFile);
	}

}