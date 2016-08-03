/**
 * @author Lisa Jul 31, 2016 TransformSketchSourceCode.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;
import java.util.TreeMap;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class SketchAssertTransformer extends AbstractSketchTransformer {
	MethodData testMethod;

	public SketchAssertTransformer(MethodData testMethod) {
		this.testMethod = testMethod;
	}

	@Override
	public void transform(LinePyGenerator utility, List<MethodData> locations) {
		// know which method to transform, know which lines should be
		// transformed.
		// for (MethodData method : locations) {
		createRepairCandidate(testMethod, locations);
		// }

	}

	private void createRepairCandidate(MethodData location,
			List<MethodData> locations) {
		if (location == null || locations == null)
			return;
		staticTransform(location, locations);

	}

}
