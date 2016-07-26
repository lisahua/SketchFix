/**
 * @author Lisa Jul 25, 2016 StateParser.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Vector;

public class StateParser {
	private HashMap<String, LinePy> lineInstr = new HashMap<String, LinePy>();

	Vector<LinePy> queue = new Vector<LinePy>();

	public Vector<LinePy> parseState(String traceFile, String stateDir) {

		try {
			parseTraceFile(traceFile);
			parseStateDir(new File(stateDir));
			updateQueue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return queue;
	}

	private void updateQueue() {
		for (int i = 0; i < queue.size(); i++) {
			queue.add(i, lineInstr.get(queue.get(i).toString()));
		}
	}

	private void parseTraceFile(String traceFile) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(traceFile));
		String line = "";
		LinePy currentLine = null;
		while ((line = reader.readLine()) != null) {
			if (isLineNumberRecord(line)) {
				if (currentLine != null) {
					queue.add(currentLine);
					lineInstr.put(currentLine.toString(), currentLine);
				}
				LinePy item = new LinePy(line);
				currentLine = item;
			} else if (line.endsWith("--"))
				continue;
			else {
				currentLine.insertState(line);
			}
		}
		reader.close();

	}

	private static boolean isLineNumberRecord(String line) {
		// line = line.replace("\"", "");
		if (line.contains("/") && line.contains("-")) {
			try {
				Integer.parseInt(line.substring(line.lastIndexOf("-") + 1));
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	private void parseStateFile(File stateFile) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(stateFile));
		String line = "";
		String key = "";
		// stateFile.getAbsolutePath()
		LinePy current = null;
		boolean flag = false;
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith("0"))
				continue;
			InstrPy instr = new InstrPy(line);
			if (instr.getInstType().equals("LDC") && isLineNumberRecord(instr.getInstSecond())) {
				flag = false;
				if (current != null) {
					lineInstr.put(key, current);
				}
				// TODO test key
				key = instr.getInstSecond();
				if (lineInstr.containsKey(key))
					flag = true;
			}
			if (flag) {
				current = lineInstr.get(key);
				// TODO if current is null, buggy
				current.addInstruction(instr);
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
