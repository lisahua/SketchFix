/**
 * @author Lisa Jul 26, 2016 StaticTracer.java 
 */
package ece.utexas.edu.sketchFix.staticParse;

import java.io.File;
import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;

public class StaticTracer {

	
	public void getStaticTracer(Vector<LinePy> trace, String sourceDir) {
		
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
	
	private void parseSourceFile(File file) {
		
	}
	
}
