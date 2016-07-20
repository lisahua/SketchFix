/**
 * @author Lisa Jul 19, 2016 ProfileFaultLocalizer.java 
 */
package ece.utexas.edu.sketchFix.slicing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class ProfileFaultLocalizer extends FaultLocalizerStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public List<LineData> locateFaultyLines(List<Vector<String>> negTraces, List<Vector<String>> posTraces) {
		HashMap<String, Integer> negCount = new HashMap<String, Integer>();
		HashMap<String, Integer> posCount = new HashMap<String, Integer>();
		Vector<String> interaction = new Vector<String>();
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
		for (Vector<String> trace : posTraces) {
			for (String s : trace) {
				if (negCount.containsKey(s)) {
					int count = 1;
					if (posCount.containsKey(s))
						count = posCount.get(s) + 1;
					posCount.put(s, count);
				}
			}
		}
		int negSize = negTraces.size();
		int posSize = posTraces.size();
		List<LineData> suspiciousLoc = new ArrayList<LineData>();
		// count Tarantula rate
		for (int i = 0; i < interaction.size(); i++) {
			String s = interaction.get(i);
			suspiciousLoc.add(new LineData(s, i, negCount.get(s) * 1.0 / negSize, posCount.get(s) * 1.0 / posSize));
		}
		Collections.sort(suspiciousLoc);
		
		return suspiciousLoc;
	}

}
