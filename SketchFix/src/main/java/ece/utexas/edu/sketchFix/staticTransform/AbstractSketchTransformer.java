/**
 * @author Lisa Jul 31, 2016 SketchTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public abstract class AbstractSketchTransformer {

	public abstract void transform(LinePyGenerator utility, List<MethodData> locations);
}
