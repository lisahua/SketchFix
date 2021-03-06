/**
 * @author Lisa Jul 31, 2016 TransformSketchSourceCode.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;
import java.util.TreeMap;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StructDefGenerator;
import sketch.compiler.ast.core.Function;

public class SketchSourceTransformer extends AbstractSketchTransformer {
	

	@Override
	public void transform(MethodData method, LinePyGenerator utility, List<MethodData> locations) {
		// know which method to transform, know which lines should be
		// transformed.
		this.utility = utility;
		// for (MethodData method : locations) {
		// DEBUG only test first location
		createRepairCandidate(method, utility.getFileLines(method.getClassFullPath()), locations);
		// }

	}

	private void createRepairCandidate(MethodData location, TreeMap<Integer, LinePy> lines,
			List<MethodData> locations) {
		if (lines == null || location == null)
			return;
		try {
			staticTransform(location, locations);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
