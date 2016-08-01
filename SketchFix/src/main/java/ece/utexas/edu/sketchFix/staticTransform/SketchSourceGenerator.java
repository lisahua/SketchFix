/**
 * @author Lisa Jul 31, 2016 SketchSourceGenerator.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.StatementAdapter;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;

public class SketchSourceGenerator {

	public void generate(TypeDeclaration clazz, MethodDeclaration method, List<ASTLinePy> astLines,
			FieldDeclaration[] fields) {

		Type returnType = method.getReturnType2();
		List<Statement> body = new ArrayList<Statement>();
		for (ASTLinePy line : astLines) {
			org.eclipse.jdt.core.dom.Statement stmt = line.statement;
			body.add((Statement) StatementAdapter.getInstance().transform(stmt));
		}

		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		StmtBlock block = new StmtBlock(AbstractASTAdapter.getContext(), body);
		creator.body(block);
		creator.name(method.getName().toString());
		List<Parameter> param = new ArrayList<Parameter>();
		
		// TODO mann harderst part
		creator.params(param);
		Function function = creator.create();

	}
	
	private void generateType() {
		
	}
	
	private void generateParam() {
		
	}
	
}
