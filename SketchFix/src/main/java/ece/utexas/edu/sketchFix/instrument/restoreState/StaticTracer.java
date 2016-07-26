/**
 * @author Lisa Jul 26, 2016 StaticTracer.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;

public class StaticTracer extends LinePyParser {

	public StaticTracer(LinePyParser parser) {
		super(parser);
	}

	private void parseSourceDir(File dir) throws Exception {
		if (!dir.exists())
			return;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				parseSourceDir(file);
			}
		} else {
			if (dir.getName().endsWith(".java"))
				parseSourceFile(dir);
		}

	}

	private void parseSourceFile(File file) throws Exception {
		String fileName = file.getCanonicalPath().replace(".", "/");
		// not in trace
		if (!files.containsKey(fileName))
			return;
		TreeMap<Integer, LinePy> lines = new TreeMap<Integer, LinePy>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		int index = 0;
		while ((line = reader.readLine()) != null) {
			index++;
			if (lines.containsKey(index)) {
				LinePy item = lines.get(index);
				item.setSourceLine(line);
			}
		}
		reader.close();
		files.put(fileName, lines);
	}

	@Override
	public void parseFiles(String[] files) {
		try {
			parseSourceDir(new File(files[0]));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
