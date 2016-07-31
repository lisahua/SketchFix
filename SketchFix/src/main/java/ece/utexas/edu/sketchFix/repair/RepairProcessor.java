/**
 * @author Lisa Jul 30, 2016 RepairProcessor.java 
 */
package ece.utexas.edu.sketchFix.repair;

import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.DynamicStateMapper;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.StaticSourceMapper;
import ece.utexas.edu.sketchFix.slicing.SliceInputCollector;

public class RepairProcessor {
	Argument argument = null;

	public RepairProcessor(String[] args) {
		argument = new Argument(args);
	}

	public void process() {

		Vector<LinePy> trace = parseTrace();
		faultLocalize(trace);
	}

	private Vector<LinePy> parseTrace() {
		String[] dirs = argument.classDir.split(",");

		String[] dyArg = new String[dirs.length + 1];
		dyArg[0] = argument.traceFile;
		for (int i = 0; i < dirs.length; i++)
			dyArg[i + 1] = dirs[i];
		DynamicStateMapper dynamicParser = new DynamicStateMapper();
		dynamicParser.parseFiles(dyArg);

		StaticSourceMapper staticParser = new StaticSourceMapper(dynamicParser);
		String[] arg = argument.sourceDir.split(",");
		staticParser.parseFiles(arg);

		Vector<LinePy> trace = staticParser.getTrace();
		return trace;
	}

	private void faultLocalize(Vector<LinePy> trace) {
		SliceInputCollector locateProcessor = new SliceInputCollector();
		// set localizer here, I use default textual now
//		String[] tokens = argument.traceFile.split(",");
//		locateProcessor.locateMethods(tokens, null);
		locateProcessor.locateMethods(trace);

	}
}
