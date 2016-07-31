/**
 * @author Lisa Jul 25, 2016 StateParser.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;
import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.instrModel.InstrPy;
import ece.utexas.edu.sketchFix.instrument.restoreState.instrModel.InstrPyBuilder;

public class DynamicStateMapper extends LinePyGenerator {
	private void parseTraceFile(String traceFile) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(traceFile));
		String line = "";
		LinePy currentLine = null;
		while ((line = reader.readLine()) != null) {
			if (isLineNumberRecord(line) > 0) {
				if (currentLine != null) {
					trace.add(currentLine);
					String file = currentLine.getFilePath();
					TreeMap<Integer, LinePy> map = (files.containsKey(file)) ? files.get(file)
							: new TreeMap<Integer, LinePy>();
					map.put(currentLine.getLineNum(), currentLine);
					files.put(file, map);
				}
				LinePy item = new LinePy(line);
				currentLine = item;
			} else if (line.endsWith("--")) {
				if (!line.startsWith("--"))
					currentLine.startNewState();
				continue;
			} else {
				currentLine.insertState(line);
			}
		}
		reader.close();
	}

	private static int isLineNumberRecord(String line) {
		line = line.replace("\"","");
		if (line.contains("/") && line.contains("-")) {
			try {
				return Integer.parseInt(line.substring(line.lastIndexOf("-") + 1));
			} catch (Exception e) {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * The instructions are static.
	 * 
	 * @param stateFile
	 * @throws Exception
	 */
	private void parseStateFile(File stateFile, String baseDir) throws Exception {
		String file = stateFile.getCanonicalPath().replace(baseDir, "").replace(".class.txt", "");
		if (!files.containsKey(file))
			return;
		TreeMap<Integer, LinePy> lines = files.get(file);
		BufferedReader reader = new BufferedReader(new FileReader(stateFile));
		String line = "";
		LinePy current = null;
		while ((line = reader.readLine()) != null) {
			// not an instruction
			if (!line.startsWith("0"))
				continue;
			InstrPy instr = new InstrPyBuilder().buildInstr(line);
			if (instr.getInstType().equals("LDC")) {
				int newLineNo = isLineNumberRecord(instr.getInstSecond());
				// not a line number line
				if (newLineNo < 0)
					continue;
				// exist in trace
				if (current != null) {
					lines.put(current.getLineNum(), current);
				}
				// update to new line number
				current = lines.get(newLineNo);
			}
			if (current != null) {
				current.addInstruction(instr);
			}
		}
		reader.close();
		files.put(file, lines);
	}

	private void parseStateDir(File dir, String baseDir) throws Exception {
		if (!dir.exists())
			return;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				parseStateDir(file, baseDir);
			}
		} else {
			if (dir.getName().endsWith(".class.txt"))
				parseStateFile(dir, baseDir);
		}

	}

	@Override
	public void parseFiles(String[] files) {
		if (trace == null)
			trace = new Vector<LinePy>();
		if (files.length < 2)
			return;
		try {
			parseTraceFile(files[0]);
			for (int i = 1; i < files.length; i++)
				parseStateDir(new File(files[i]), files[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
