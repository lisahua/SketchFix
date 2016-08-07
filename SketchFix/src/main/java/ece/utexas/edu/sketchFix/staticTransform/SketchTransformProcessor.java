/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class SketchTransformProcessor {

	public void process(LinePyGenerator generator, List<MethodData> locations, MethodData testMethod,
			String outputFile) {
		AbstractSketchTransformer assertTran = new SketchAssertTransformer(testMethod);
		assertTran.transform(testMethod, generator, locations);
		assertTran.writeToFile(outputFile+"2");
	
		
		
		AbstractSketchTransformer sourceTran = new SketchSourceTransformer();
		sourceTran.transform(locations.get(0), generator, locations);
		sourceTran.writeToFile(outputFile);
		// transform sketch assertion
		
		
		assertTran.setMethods(sourceTran.getMethods());
		assertTran.setStructs(sourceTran.getStructs());
		
		assertTran.writeToFile(outputFile+"2");
	}

}