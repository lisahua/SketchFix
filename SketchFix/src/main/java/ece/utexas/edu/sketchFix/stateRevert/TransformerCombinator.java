/**
 * @author Lisa Aug 16, 2016 TransformerCombinator.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ece.utexas.edu.sketchFix.staticTransform.AbstractSketchTransformer;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.Annotation;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.StructDef.TStructCreator;
import sketch.util.datastructures.HashmapList;

public class TransformerCombinator extends TransHandler {
	AbstractSketchTransformer ass;
	AbstractSketchTransformer transformer;

	public TransformerCombinator(AbstractSketchTransformer assTransformer,
			AbstractSketchTransformer sourceTransformer) {
		ass = assTransformer;
		transformer = sourceTransformer;
		init();
	}

	protected void init() {
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
		TStructCreator creator = new TStructCreator(AbstractASTAdapter.getContext2());
		creator.name(one.getName());
		List<String> names = new ArrayList<String>();
		List<Type> types = new ArrayList<Type>();
		for (Map.Entry<String, Type> entry : one.getFieldTypMap()) {
			names.add(entry.getKey());
			types.add(entry.getValue());
		}
		for (Map.Entry<String, Type> entry : two.getFieldTypMap()) {
			if (names.contains(entry.getKey()))
				continue;
			names.add(entry.getKey());
			types.add(entry.getValue());
		}
		HashmapList<String, Annotation> annotations = new HashmapList<String, Annotation>();
		creator.annotations(annotations);
		creator.fields(names, types);
		return creator.create();
	}

	private Function mergeTwoMethod(Function one, Function two) {
		if (one.toString().length() <= two.toString().length())
			return two;
		return one;
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
