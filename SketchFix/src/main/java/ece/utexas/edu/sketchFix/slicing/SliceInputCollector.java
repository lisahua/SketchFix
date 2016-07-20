/**
 * @author Lisa Jul 19, 2016 SliceInput.java 
 */
package ece.utexas.edu.sketchFix.slicing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

public class SliceInputCollector {
	FaultLocalizerStrategy localizer = new ProfileFaultLocalizer();

	public List<LineData> compareTraces(String[] negFiles, String[] posFiles) {
		if (negFiles == null || posFiles == null)
			return localizer.locateFaultyLines(null, null);
		return localizer.locateFaultyLines(readNegFiles(negFiles), readPosFiles(posFiles));
	}

	public List<Vector<String>> readNegFiles(String[] file) {
		List<Vector<String>> negTrace = new ArrayList<Vector<String>>();
		for (String s : file) {
			negTrace.add(readFile(s));
		}
		return negTrace;
	}

	public List<Vector<String>> readPosFiles(String[] file) {
		List<Vector<String>> posTrace = new ArrayList<Vector<String>>();
		for (String s : file) {
			posTrace.add(readFile(s));
		}
		return posTrace;
	}

	private Vector<String> readFile(String path) {
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

	public void setLocalizer(String[] option) {
		if (option.length < 1)
			return;
		if (option[0].equals("naive"))
			localizer = new NaiveFaultLocalizer(option);
	}
}
