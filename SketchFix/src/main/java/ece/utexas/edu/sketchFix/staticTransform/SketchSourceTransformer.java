/**
 * @author Lisa Jul 31, 2016 TransformSketchSourceCode.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.StructDefGenerator;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;
import sketch.compiler.ast.core.typs.StructDef;

public class SketchSourceTransformer extends AbstractSketchTransformer {

	@Override
	public void transform(LinePyGenerator utility, List<MethodData> locations) {
		// know which method to transform, know which lines should be
		// transformed.
		for (MethodData method : locations) {
			createRepairCandidate(method, utility.getFileLines(method.getClassFullPath()), locations);
		}

	}

	private void createRepairCandidate(MethodData location, TreeMap<Integer, LinePy> lines,
			List<MethodData> locations) {
		if (lines == null || location == null)
			return;
		staticTransform(location, locations);
		Program prog = generate();
		try {
			prog.accept(new SimpleSketchFilePrinter("tmp.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Program generate() {
		MethodDeclarationAdapter mtdDecl = new MethodDeclarationAdapter(type, type.getFields(), astLines);
		Function function = (Function) mtdDecl.transform(currentMtd);
		// TODO create structDef correspondingly
		methods.addAll(StructDefGenerator.createMethods(locations));
		methods.add(function);
	structs.addAll(StructDefGenerator.createStructs());
		Program empty = Program.emptyProgram();
		sketch.compiler.ast.core.Package pkg = new sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
				structs, new ArrayList<FieldDecl>(), methods, new ArrayList<StmtSpAssert>());
		List<sketch.compiler.ast.core.Package> pkgList = new ArrayList<sketch.compiler.ast.core.Package>();
		pkgList.add(pkg);

		ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new HashSet<Directive>());
		return progCreator.create();

	}

}
