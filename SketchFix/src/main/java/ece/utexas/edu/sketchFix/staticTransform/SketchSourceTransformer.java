/**
 * @author Lisa Jul 31, 2016 TransformSketchSourceCode.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;
import java.util.TreeMap;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class SketchSourceTransformer extends AbstractSketchTransformer {

	@Override
	public void transform(LinePyGenerator utility, List<MethodData> locations) {
		// know which method to transform, know which lines should be
		// transformed.
		for (MethodData method : locations) {
			createRepairCandidate(method, utility.getFileLines(method.getClassFullPath()));
		}

	}

	private void createRepairCandidate(MethodData location, TreeMap<Integer, LinePy> lines) {
		if (lines == null || location == null)
			return;
		ASTTransformer astTransform = new ASTTransformer();
		astTransform.staticTransform(location);
	}

}
