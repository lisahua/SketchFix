/**
 * @author Lisa Jul 26, 2016 LinePyParserDecorator.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

public abstract class LinePyParser {
	protected Vector<LinePy> trace = new Vector<LinePy>();
	protected HashMap<String,TreeMap<Integer, LinePy>> files = new HashMap<String,TreeMap<Integer, LinePy>>();

	public LinePyParser() {

	}

	public LinePyParser(LinePyParser parser) {
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

}
