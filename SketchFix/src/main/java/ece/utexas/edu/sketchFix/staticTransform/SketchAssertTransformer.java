/**
 * @author Lisa Jul 31, 2016 TransformSketchSourceCode.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class SketchAssertTransformer extends AbstractSketchTransformer {
	MethodData testMethod;

	public SketchAssertTransformer(MethodData testMethod) {
		this.testMethod = testMethod;
	}

	@Override
	public void transform(MethodData method,LinePyGenerator utility, List<MethodData> locations) {
		// know which method to transform, know which lines should be
		// transformed.
		// for (MethodData method : locations) {
		try {
			staticTransform(method, locations);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// }

	}

}
