/**
 * @author Lisa Jul 26, 2016 StaticTracer.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.TreeMap;

public class StaticSourceMapper extends LinePyGenerator {

	public StaticSourceMapper(LinePyGenerator parser) {
		super(parser);
	}

	private void parseSourceDir(File dir, String baseDir) throws Exception {
		if (!dir.exists())
			return;
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				parseSourceDir(file, baseDir);
			}
		} else {
			if (dir.getName().endsWith(".java"))
				parseSourceFile(dir, baseDir);
		}

	}

	private void parseSourceFile(File file, String baseDir) throws Exception {
		String fileName = file.getCanonicalPath().replace(baseDir, "").replace(".java", "");
		// not in trace
		if (!files.containsKey(fileName))
			return;
		TreeMap<Integer, LinePy> lines = files.get(fileName);
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
			for (String s : files)
				parseSourceDir(new File(s), s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
