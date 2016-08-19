/**
 * @author Lisa Aug 16, 2016 StateReverter.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.AbstractSketchTransformer;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;

public class TransformPostProcessor {
	Program prog = null;

	public TransformPostProcessor(AbstractSketchTransformer transformer) {

		Program empty = Program.emptyProgram();
		sketch.compiler.ast.core.Package pkg = new sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
				transformer.getMergeStructs(), new ArrayList<FieldDecl>(), transformer.getMergeMethods(),
				new ArrayList<StmtSpAssert>());
		List<sketch.compiler.ast.core.Package> pkgList = new ArrayList<sketch.compiler.ast.core.Package>();
		pkgList.add(pkg);

		ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new HashSet<Directive>());
		prog = progCreator.create();

		InheritanceReplacer inheritReplacer = new InheritanceReplacer();
		prog = (Program) inheritReplacer.visitProgram(prog);
	}

	public void writeToFile(String outputFile) {
		if (prog == null)
			return;
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	public Program getProgram() {
		return prog;
	}
}
