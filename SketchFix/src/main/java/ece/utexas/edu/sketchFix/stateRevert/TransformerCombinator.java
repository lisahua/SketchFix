/**
 * @author Lisa Aug 16, 2016 TransformerCombinator.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.AbstractSketchTransformer;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;
import sketch.compiler.ast.core.typs.StructDef;

public class TransformerCombinator {
	protected List<Function> methods = new ArrayList<Function>();
	protected List<StructDef> structs = new ArrayList<StructDef>();

	public TransformerCombinator(AbstractSketchTransformer assTransformer,
			AbstractSketchTransformer sourceTransformer) {
		mergeAnotherTransformer(assTransformer, sourceTransformer);
	}

	private void mergeAnotherTransformer(AbstractSketchTransformer ass, AbstractSketchTransformer transformer) {
		HashMap<String, StructDef> curStruct = new HashMap<String, StructDef>();
		HashMap<String, Function> curMtd = new HashMap<String, Function>();

		for (StructDef strct : ass.getStructs())
			curStruct.put(strct.getName(), strct);
		for (Function func : ass.getMethods())
			curMtd.put(func.getName(), func);

		for (StructDef strct : transformer.getStructs()) {
			if (curStruct.containsKey(strct.getName())) {
				curStruct.put(strct.getName(), mergeTwoStructs(strct, curStruct.get(strct.getName())));
			} else
				curStruct.put(strct.getName(), strct);
		}

		for (Function func : transformer.getMethods()) {
			if (curMtd.containsKey(func.getName())) {
				curMtd.put(func.getName(), mergeTwoMethod(func, curMtd.get(func.getName())));
			} else
				curMtd.put(func.getName(), func);
		}

		structs.addAll(curStruct.values());
		methods.addAll(curMtd.values());
	}

	private StructDef mergeTwoStructs(StructDef one, StructDef two) {
		// FIXME I know buggy
		if (one.toString().length() <= two.toString().length())
			return two;
		return one;
	}

	private Function mergeTwoMethod(Function one, Function two) {
		if (one.toString().length() <= two.toString().length())
			return two;
		return one;
	}

	public List<Function> getMethods() {
		return methods;
	}

	public List<StructDef> getStructs() {
		return structs;
	}

	public void writeToFile(String outputFile) {
		Program empty = Program.emptyProgram();
		sketch.compiler.ast.core.Package pkg = new sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
				structs, new ArrayList<FieldDecl>(), methods, new ArrayList<StmtSpAssert>());
		List<sketch.compiler.ast.core.Package> pkgList = new ArrayList<sketch.compiler.ast.core.Package>();
		pkgList.add(pkg);

		ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new HashSet<Directive>());
		Program prog = progCreator.create();
		try {
			prog.accept(new SimpleSketchFilePrinter(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
