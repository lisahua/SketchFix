/**
 * @author Lisa Jul 26, 2016 LinePyParserDecorator.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

public abstract class LinePyGenerator {
	protected Vector<LinePy> trace = new Vector<LinePy>();
	protected HashMap<String, TreeMap<Integer, LinePy>> files = new HashMap<String, TreeMap<Integer, LinePy>>();

	public LinePyGenerator() {

	}

	public LinePyGenerator(LinePyGenerator parser) {
		if (parser == null)
			return;
		this.trace = parser.trace;
		this.files = parser.files;

	}

	public abstract void parseFiles(String[] files);

	public Vector<LinePy> getTrace() {
		return trace;
	}

	protected void setTrace(Vector<LinePy> trace) {
		this.trace = trace;
	}

	public HashMap<String, TreeMap<Integer, LinePy>> getFiles() {
		return files;
	}

	public TreeMap<Integer, LinePy> getFileLines(String file) {
		return files.get(file);

	}

	public LinePy getFileLinePy(String file, int num) {
		return getFileLines(file).get(num);
	}
}
