/**
 * @author Lisa Jul 31, 2016 SketchSourceGenerator.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

public class SketchSourceGenerator {

	public void generate(TypeDeclaration clazz, MethodDeclaration method, List<ASTLinePy> astLines, FieldDeclaration[] fields) {
		Type returnType = method.getReturnType2();
	for (ASTLinePy line: astLines) {
		Statement stmt = line.statement;
		if (stmt instanceof VariableDeclarationStatement) {
			
		} else if (stmt instanceof IfStatement) {
			
		} else if (stmt instanceof ReturnStatement) {
			
		} else if (stmt instanceof WhileStatement) {
			
		}
	}
	}
}
