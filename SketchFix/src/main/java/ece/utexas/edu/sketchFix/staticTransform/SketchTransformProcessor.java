/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;
import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.repair.Argument;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class SketchTransformProcessor {
	private Argument arg = null;
	private Vector<LinePy> trace;

	public SketchTransformProcessor(Argument argument) {
		arg = argument;
	}

	public void process(LinePyGenerator generator, List<MethodData> locations, MethodData testMethod,
			String outputFile) {
		AbstractSketchTransformer assertTran = new SketchAssertTransformer(testMethod);
		// FIXME buggy
		testMethod.setBasrDirs(arg.getSourceDir());
		testMethod.setBaseDir(arg.getSourceDir()[1]);
		assertTran.transform(testMethod, generator, locations);
		// assertTran.writeToFile(outputFile+"2");

		AbstractSketchTransformer sourceTran = new SketchSourceTransformer();
		MethodData data = locations.get(0);
		data.setBaseDir(arg.getSourceDir()[0]);
		data.setBasrDirs(arg.getSourceDir());
		sourceTran.transform(locations.get(0), generator, locations);
		assertTran.mergeAnotherTransformer(sourceTran);
		assertTran.writeToFile(outputFile);
		// transform sketch assertion

	}

}