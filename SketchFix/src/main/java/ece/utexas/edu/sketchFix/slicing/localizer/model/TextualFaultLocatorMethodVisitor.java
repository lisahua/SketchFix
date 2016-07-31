/**
 * @author Lisa Jul 30, 2016 TextualFaultLocatorMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer.model;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

@Deprecated
public class TextualFaultLocatorMethodVisitor extends ASTVisitor {
	MethodData data = null;

	public TextualFaultLocatorMethodVisitor(MethodData method) {
		String filePath = method.classFullPath;
		File file = new File(filePath);
		if (!file.exists())
			return;
		data = method;
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		try {
			fileString = FileUtils.readFileToString(file);
			parser.setSource(fileString.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			cu.accept(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean visit(MethodDeclaration mv) {
		if (data == null)
			return super.visit(mv);
		if (!mv.getName().toString().equals(data.methodName))
			return super.visit(mv);
		mv.accept(new InvokeVisitor());

		return super.visit(mv);
	}

	public boolean visit(ImportDeclaration node) {
//		data.insertNamePath(node.getName().getFullyQualifiedName());
		return super.visit(node);
	}

	class InvokeVisitor extends ASTVisitor {
		
		HashMap<String,String> nameType = new HashMap<String, String>();
		public boolean visit(MethodInvocation mi) {
			String mName = mi.getName().toString();
			Expression exp = mi.getExpression();
			if (exp instanceof Name) {
				String type = nameType.get(exp.toString());
				
			}
			return super.visit(mi);
		}

		public boolean visit(VariableDeclarationStatement vd) {
			return super.visit(vd);
		}

		public boolean visit(Assignment assign) {

			return super.visit(assign);
		}
	}
}
