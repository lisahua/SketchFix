/**
 * @author Lisa Jul 19, 2016 ProfileFaultLocalizer.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import ece.utexas.edu.sketchFix.slicing.LineData;

public class ProfileFaultLocalizer extends FaultLocalizerStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public List<LineData> locateFaultyLines(String[] negPath, String[] posPath) {
		HashMap<String, Integer> negCount = new HashMap<String, Integer>();
		HashMap<String, Integer> posCount = new HashMap<String, Integer>();
		Vector<String> interaction = new Vector<String>();
		List<Vector<String>> negTraces = readFiles(negPath);
		// Merge negTraces
		for (int i = 0; i < negTraces.size(); i++) {
			for (String s : negTraces.get(i)) {
				int count = 1;
				if (negCount.containsKey(s))
					count = negCount.get(s) + 1;
				else
					interaction.add(s);
				negCount.put(s, count);
			}
		}
		// compare neg with all posTraces
		for (String path : posPath) {
			for (String s : readFile(path)) {
				if (negCount.containsKey(s)) {
					int count = 1;
					if (posCount.containsKey(s))
						count = posCount.get(s) + 1;
					posCount.put(s, count);
				}
			}
		}
		int negSize = negPath.length;
		int posSize = posPath.length;
		List<LineData> suspiciousLoc = new ArrayList<LineData>();
		// count Tarantula rate
		for (int i = 0; i < interaction.size(); i++) {
			String s = interaction.get(i);
			if (!posCount.containsKey(s))
				suspiciousLoc.add(new LineData(s, i, negCount.get(s) * 1.0 / negSize, 0));
			else
				suspiciousLoc.add(new LineData(s, i, negCount.get(s) * 1.0 / negSize, posCount.get(s) * 1.0 / posSize));
		}
		Collections.sort(suspiciousLoc);

		return suspiciousLoc;
	}

}
