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
import ece.utexas.edu.sketchFix.repair.postProcessor.RepairTransformer;
import ece.utexas.edu.sketchFix.repair.postProcessor.SketchRepairValidator;
import ece.utexas.edu.sketchFix.repair.postProcessor.SketchRewriterProcessor;
import ece.utexas.edu.sketchFix.repair.processor.SketchSynthesizer;
import ece.utexas.edu.sketchFix.slicing.SliceInputCollector;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.SketchTransformProcessor;
import sketch.compiler.ast.core.Program;

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
		SketchTransformProcessor transformer = new SketchTransformProcessor(argument);
		Program prog = transformer.process(generator, locations, testMethod, argument.skOrigin);
		if (prog == null) {
			System.out.println("[Sketch Transformation] Failure!");
			return;
		} else {
			System.out.println("[Step 2: Sketch Transformation] File: " + argument.skOrigin + ". Done!");
		}
		// first invoke synthesis
		SketchSynthesizer processor = new SketchSynthesizer(prog);
		prog = processor.process(argument.skOrigin);
		if (prog == null) {
			System.out.println("[Sketch Repair] Unable to repair this type of error !");
			return;
		} else {
			System.out.println("[Step 3: Sketch Repair] Start to generate repair candidates!");
		}
		// check repair candidates
		SketchRepairValidator validator = new SketchRepairValidator(prog, transformer.getLines(), processor.getScope());
		RepairTransformer rewriter = validator.process(argument.skOrigin + "__");
		if (rewriter == null) {
			System.out.println("[Step 3: Sketch Repair] Fail in all repair candidates !");
			return;
		} else {
			System.out.println("[Ste 4: Sketch Repair] Successfully generate repair!");
		}
		SketchRewriterProcessor skRewriter = new SketchRewriterProcessor(rewriter, locations.get(0));
		skRewriter.process();
		System.out.println("[Sketch Repair] Done with Repair!");

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

		// Vector<LinePy> trace = staticParser.getTrace();
		return staticParser;
	}

	private List<MethodData> faultLocalize(Vector<LinePy> trace) {
		SliceInputCollector locateProcessor = new SliceInputCollector();
		List<MethodData> locations = locateProcessor.locateMethods(trace);
		testMethod = locateProcessor.getTestMethod();
		return locations;
	}
}
