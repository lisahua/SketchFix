/**
 * @author Lisa Jul 19, 2016 NaiveFaultLocalizer.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.slicing.localizer.model.LineData;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class TextualFaultLocalizer extends FaultLocalizerStrategy {
	HashMap<String, MethodData> methodMap = new HashMap<String, MethodData>();
	Stack<String> methodOrder = new Stack<String>();

	@Override
	public List<LineData> locateFaultyLines(String[] negTraces, String[] posTPath) {
		return null;
	}

	@Override
	public List<MethodData> locateFaultyMethods(String[] negFiles, String[] posTPath) {
		return null;
	}

	private Vector<MethodData> textPrioritize(Vector<LinePy> trace) {
		MethodComparator comp = null;
		// init test method
		for (int i = methodOrder.size() - 1; i >= 0; i--) {
			MethodData mdata = methodMap.get(methodOrder.get(i));
			// MethodData mdata = methodMap.get(methodOrder.peek());
			if (mdata.isTestMethod()) {
				comp = new MethodComparator(mdata);
				testMethod = mdata;
				break;
			}
		}
		// Prioritize last lines
		Vector<MethodData> list = new Vector<MethodData>();

		while (!methodOrder.isEmpty() && list.size() < LocalizerUtility.MAX_METHOD_THRESHOLD) {
			MethodData data = methodMap.get(methodOrder.pop());
			if (data.isTestMethod())
				continue;
			if (!list.contains(data))
				list.add(data);
		}
		if (comp != null)
			list.sort(comp);
		return list;
	}

	@Override
	public Vector<MethodData> locateFaultyMethods(Vector<LinePy> trace) {
		// System.out.println(trace.lastElement());
		for (LinePy oneLine : trace) {
			String key = oneLine.getFilePath() + "-" + oneLine.getMethodName() + "-" + oneLine.getParams();
			MethodData data = (methodMap.containsKey(key)) ? methodMap.get(key) : new MethodData(key);
			data = data.setLinePy(oneLine);
			methodMap.put(key, data);

			if (methodOrder.contains(key))
				methodOrder.remove(key);
			methodOrder.push(key);
		}

		return textPrioritize(trace);
	}

	class MethodComparator implements Comparator<MethodData> {
		MethodData data = null;

		public MethodComparator(MethodData testMtd) {
			data = testMtd;
		}

		@Override
		public int compare(MethodData o1, MethodData o2) {
			// FIXME haven't fully tested
			if (o1.namingSimilarity(data) < o2.namingSimilarity(data))
				return 0;
			else
				return 1;
		}

	}
}
