/**
 * @author Lisa Jul 19, 2016 SliceInput.java 
 */
package ece.utexas.edu.sketchFix.slicing;

import java.util.List;

import ece.utexas.edu.sketchFix.slicing.localizer.FaultLocalizerStrategy;
import ece.utexas.edu.sketchFix.slicing.localizer.NaiveFaultLocalizer;
import ece.utexas.edu.sketchFix.slicing.localizer.TextualFaultLocalizer;
import ece.utexas.edu.sketchFix.slicing.localizer.model.LineData;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class SliceInputCollector {
	FaultLocalizerStrategy localizer = new TextualFaultLocalizer();

	public List<LineData> compareTraces(String[] negFiles, String[] posFiles) {
		if (negFiles == null || posFiles == null)
			return localizer.locateFaultyLines(null, null);
		return localizer.locateFaultyLines(negFiles, posFiles);
	}

	public List<MethodData> locateMethods(String[] negFiles, String[] posFiles) {
		if (negFiles == null)
			return localizer.locateFaultyMethods(null, null);
		return localizer.locateFaultyMethods(negFiles, posFiles);
	}
	
	public void setLocalizer(String[] option) {
		if (option.length < 1)
			return;
		if (option[0].equals("naive"))
			localizer = new NaiveFaultLocalizer(option);
	}
}
