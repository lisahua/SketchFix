/**
 * @author Lisa Jul 19, 2016 NaiveFaultLocalizer.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import ece.utexas.edu.sketchFix.slicing.localizer.model.LineData;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class TextualFaultLocalizer extends FaultLocalizerStrategy {
	private HashMap<String, MethodData> methods = new HashMap<String, MethodData>();

	public TextualFaultLocalizer() {

	}

	@Override
	public List<LineData> locateFaultyLines(String[] negTraces, String[] posTPath) {
		return null;
	}

	@Override
	public List<MethodData> locateFaultyMethods(String[] negFiles, String[] posTPath) {
		List<Vector<String>> negTraces = readFiles(negFiles);
		for (Vector<String> oneTrace : negTraces) {
			for (String s : oneTrace) {
				String key = s.substring(0, s.lastIndexOf("-"));
				MethodData data = (methods.containsKey(key)) ? methods.get(key) : new MethodData(key);
				data.insertCount();
				methods.put(key, data);
			}
		}
		return null;
	}

	
	
}
