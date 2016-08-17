/**
 * @author Lisa Aug 16, 2016 TransHandler.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;
import sketch.compiler.ast.core.typs.StructDef;

public abstract class TransHandler {
	protected List<Function> methods = new ArrayList<Function>();
	protected List<StructDef> structs = new ArrayList<StructDef>();

	public List<Function> getMethods() {
		return methods;
	}

	public List<StructDef> getStructs() {
		return structs;
	}

	public Program generateProg() {
		Program empty = Program.emptyProgram();
		sketch.compiler.ast.core.Package pkg = new sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
				structs, new ArrayList<FieldDecl>(), methods, new ArrayList<StmtSpAssert>());
		List<sketch.compiler.ast.core.Package> pkgList = new ArrayList<sketch.compiler.ast.core.Package>();
		pkgList.add(pkg);

		ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new HashSet<Directive>());
		return  progCreator.create();
	}

	protected abstract void init();

	public void writeToFile(String outputFile) {
		Program prog = generateProg();
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
