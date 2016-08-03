/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
@Deprecated
public class SketchTransformProcessor {

	public void process(LinePyGenerator generator, List<MethodData> locations, MethodData testMethod) {
		AbstractSketchTransformer transformer = new SketchSourceTransformer();
		transformer.transform(generator, locations);
		
		
		// transform sketch assertion
		transformer = new SketchAssertTransformer(testMethod);
		transformer.transform(generator, locations);
		transformer.writeToFile("tmp.txt");
	}

}