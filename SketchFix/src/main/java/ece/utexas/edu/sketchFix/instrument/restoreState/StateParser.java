/**
 * @author Lisa Jul 25, 2016 StateParser.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;

public class StateParser {
	private HashMap<String, TreeSet<Integer>> touchLines = new HashMap<String, TreeSet<Integer>>();
	private HashMap<String, LinePy> lineInstr = new HashMap<String, LinePy>();

	Queue<LinePy> queue = new LinkedList<LinePy>();

	public void parseState(String traceFile, String stateFile) {
		try {
			parseTraceFile(traceFile);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseTraceFile(String traceFile) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(traceFile));
		String line = "";
		while ((line = reader.readLine()) != null) {
			LinePy item = new LinePy(line);
			queue.add(item);
			TreeSet<Integer> set = (touchLines.containsKey(item.getFilePath())) ? touchLines.get(item.getFilePath())
					: new TreeSet<Integer>();
			set.add(item.getLineNum());
			touchLines.put(item.getFilePath(), set);
			lineInstr.put(item.toString(), item);
		}
		reader.close();
	}

	private void parseStateFile(File stateFile) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(stateFile));
		// TODO test this key
		String key = stateFile.getCanonicalPath();
		TreeSet<Integer> lineNum = touchLines.get(key);
		String line = "";
		List<String> instr = new ArrayList<String>();
		int flag = -1;
		// stateFile.getAbsolutePath()
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith("0"))
				continue;
			
			if (flag > 0) {

			}
		}
		reader.close();
	}

	private void parseStateDir(File dir) throws Exception {
		if (!dir.exists())
			return;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				parseStateDir(file);
			}
		} else {
			if (dir.getName().endsWith(".class.txt"))
				parseStateFile(dir);
		}

	}
}
