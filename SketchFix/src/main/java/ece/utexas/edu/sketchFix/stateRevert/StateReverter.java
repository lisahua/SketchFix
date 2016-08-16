/**
 * @author Lisa Aug 16, 2016 StateReverter.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import ece.utexas.edu.sketchFix.staticTransform.AbstractSketchTransformer;

public class StateReverter {
	TransformerCombinator combinator = null;

	public StateReverter(AbstractSketchTransformer assertTran, AbstractSketchTransformer sourceTran) {
		combinator = new TransformerCombinator(assertTran, sourceTran);
	}

	public void writeToFile(String outputFile) {
		combinator.writeToFile(outputFile);

	}

}
