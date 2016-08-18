/**
 * @author Lisa Aug 17, 2016 TransformHandler.java 
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

public class TransformHandler {
	AbstractSketchTransformer transformer;

	public TransformHandler(AbstractSketchTransformer skTransform) {
		transformer = skTransform;
	}

	public Program generateProg() {
		Program empty = Program.emptyProgram();
		sketch.compiler.ast.core.Package pkg = new sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
				transformer.getMergeStructs(), new ArrayList<FieldDecl>(), transformer.getMergeMethods(),
				new ArrayList<StmtSpAssert>());
		List<sketch.compiler.ast.core.Package> pkgList = new ArrayList<sketch.compiler.ast.core.Package>();
		pkgList.add(pkg);

		ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new HashSet<Directive>());
		return progCreator.create();
	}

	public void writeToFile(String outputFile) {
		Program prog = generateProg();
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
