/**
 * @author Lisa Jul 31, 2016 SketchSourceGenerator.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;
import sketch.compiler.ast.core.typs.StructDef;

@Deprecated
public class SketchSourceGenerator {

	public Program generate(TypeDeclaration clazz, MethodDeclaration method, List<ASTLinePy> astLines,
			FieldDeclaration[] fields, List<?> imports,List<MethodData> locations) {
		MethodDeclarationAdapter mtdDecl = new MethodDeclarationAdapter(clazz, fields, astLines);
		Function function = (Function) mtdDecl.transform(method);
		// TODO create structDef correspondingly
		List<Function> methods = StructDefGenerator.createMethods(locations);
		methods.add(function);
		List<StructDef> structs = StructDefGenerator.createStructs();
		Program empty = Program.emptyProgram();
		sketch.compiler.ast.core.Package pkg = new sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
				structs, new ArrayList<FieldDecl>(), methods, new ArrayList<StmtSpAssert>());
		List<sketch.compiler.ast.core.Package> pkgList = new ArrayList<sketch.compiler.ast.core.Package>();
		pkgList.add(pkg);

		ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new HashSet<Directive>());
		return progCreator.create();

	}

}
