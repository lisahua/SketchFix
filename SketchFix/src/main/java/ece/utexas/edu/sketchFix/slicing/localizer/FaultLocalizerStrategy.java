/**
 * @author Lisa Jul 19, 2016 FaultLocalizerStrategy.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.slicing.localizer.model.LineData;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public abstract class FaultLocalizerStrategy {

	public abstract List<LineData> locateFaultyLines(String[] negTraces, String[] posTPath);

	public abstract List<MethodData> locateFaultyMethods(String[] negTraces, String[] posTPath);

	public abstract Vector<MethodData> locateFaultyMethods(Vector<LinePy> trace);

	protected Vector<String> readFile(String path) {
		Vector<String> trace = new Vector<String>();
		String line = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			while ((line = reader.readLine()) != null) {
				trace.addElement(line);
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return trace;
	}

	protected List<Vector<String>> readFiles(String[] path) {
		List<Vector<String>> traces = new ArrayList<Vector<String>>();
		String line = "";
		BufferedReader reader = null;
		try {
			for (String s : path) {
				Vector<String> trace = new Vector<String>();
				reader = new BufferedReader(new FileReader(s));
				while ((line = reader.readLine()) != null) {
					trace.addElement(line);
				}
				reader.close();
				traces.add(trace);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return traces;
	}
}
