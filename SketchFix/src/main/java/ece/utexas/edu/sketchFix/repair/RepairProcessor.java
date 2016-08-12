/**
 * @author Lisa Jul 30, 2016 RepairProcessor.java 
 */
package ece.utexas.edu.sketchFix.repair;

import java.util.List;
import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.DynamicStateMapper;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.instrument.restoreState.StaticSourceMapper;
import ece.utexas.edu.sketchFix.slicing.SliceInputCollector;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.SketchTransformProcessor;

public class RepairProcessor {
	Argument argument = null;
	private MethodData testMethod;

	public RepairProcessor(String[] args) {
		argument = new Argument(args);
	}

	public void process() {
		// parse source code, trace, and state
		LinePyGenerator generator = parseTrace();
		Vector<LinePy> trace = generator.getTrace();
		// localize faults
		List<MethodData> locations = faultLocalize(trace);
		// transform to sketch front end
		new SketchTransformProcessor(argument).process(generator, locations, testMethod, argument.skOrigin);

		// map repair back

	}

	private LinePyGenerator parseTrace() {
		String[] dirs = argument.classDir;

		String[] dyArg = new String[dirs.length + 1];
		dyArg[0] = argument.traceFile;
		for (int i = 0; i < dirs.length; i++)
			dyArg[i + 1] = dirs[i];
		DynamicStateMapper dynamicParser = new DynamicStateMapper();
		dynamicParser.parseFiles(dyArg);

		StaticSourceMapper staticParser = new StaticSourceMapper(dynamicParser);
		String[] arg = argument.sourceDir;
		staticParser.parseFiles(arg);

		Vector<LinePy> trace = staticParser.getTrace();
		return staticParser;
	}

	private List<MethodData> faultLocalize(Vector<LinePy> trace) {
		SliceInputCollector locateProcessor = new SliceInputCollector();
		List<MethodData> locations = locateProcessor.locateMethods(trace);
		testMethod = locateProcessor.getTestMethod();
		return locations;
	}
}
