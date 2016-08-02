/**
 * @author Lisa Jul 31, 2016 SketchSourceGenerator.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.StructDefAdapter;
import sketch.compiler.ast.core.Function;

public class SketchSourceGenerator {

	public void generate(TypeDeclaration clazz, MethodDeclaration method, List<ASTLinePy> astLines,
			FieldDeclaration[] fields, List<?> imports) {
		MethodDeclarationAdapter mtdDecl = new MethodDeclarationAdapter(clazz, fields, astLines);
		Function function = (Function) mtdDecl.transform(method);
		// TODO create structDef correspondingly
		StructDefAdapter.createMethods();
		StructDefAdapter.createStructs();
	}

}
