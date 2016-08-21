/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.io.FileNotFoundException;
import java.util.List;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.repair.Argument;
import ece.utexas.edu.sketchFix.repair.postProcessor.SketchRepairValidator;
import ece.utexas.edu.sketchFix.repair.processor.SketchSynthesizer;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.stateRevert.StateInsertProcessor;
import ece.utexas.edu.sketchFix.stateRevert.TransformPostProcessor;
import sketch.compiler.ast.core.Program;

public class SketchTransformProcessor {
	private Argument arg = null;
	// private Vector<LinePy> trace;

	public SketchTransformProcessor(Argument argument) {
		arg = argument;
	}

	public void process(LinePyGenerator generator, List<MethodData> locations, MethodData testMethod,
			String outputFile) {
		AbstractSketchTransformer assertTran = new SketchAssertTransformer(testMethod);
		// FIXME buggy
		testMethod.setBasrDirs(arg.getSourceDir());
		testMethod.setBaseDir(arg.getSourceDir()[1]);
		// transform sketch assertion
		assertTran.transform(testMethod, generator, locations);
		// StmtStateMapper assState = assertTran.getStateMapper();

		AbstractSketchTransformer sourceTran = new SketchSourceTransformer();
		MethodData data = locations.get(0);
		data.setBaseDir(arg.getSourceDir()[0]);
		data.setBasrDirs(arg.getSourceDir());
		sourceTran.setRefTransformer(assertTran);
		sourceTran.transform(locations.get(0), generator, locations);
		// StmtStateMapper sourceState = sourceTran.getStateMapper();

		TransformPostProcessor reverter = new TransformPostProcessor(sourceTran);
		reverter.writeToFile(outputFile);

		Program prog = reverter.getProgram();
		// ConditionTraceReplacer replacer = new
		// ConditionTraceReplacer(assertTran.getStateMapper().getLinePyList(),
		// sourceTran.getStateMapper().getLinePyList());
		// prog = (Program) replacer.visitProgram(prog);
		StateInsertProcessor replacer = new StateInsertProcessor(assertTran.getStateMapper().getLinePyList(),
				sourceTran.getStateMapper().getLinePyList());
		prog = (Program) replacer.visitProgram(prog);
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile + "2"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		SketchSynthesizer processor = new SketchSynthesizer(prog);
//		processor.forTest( "/Users/lisahua/Documents/lisa/project/build/Chart14_buggy/.tmp/sketchOrig3.sk3");
		
		prog = processor.process(outputFile+"2");
		
		SketchRepairValidator validator = new SketchRepairValidator(prog,assertTran.getStateMapper().getLinePyList(),
				sourceTran.getStateMapper().getLinePyList(),processor.getAddedNode());
		validator.process(outputFile + "3");
		
		
	}

}