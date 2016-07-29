/**
 * @author Lisa Jul 29, 2016 RewriteModel.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.util.HashSet;

import javax.lang.model.element.Modifier;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

public class RewriteModel {
	TypeDeclaration node = null;

	public RewriteModel(TypeDeclaration node) {
		this.node = node;
		init();
	}

	private void init() {

		FieldDeclaration[] fields = node.getFields();
		MethodDeclaration[] methods = node.getMethods();
		HashSet<String> mNameSet = new HashSet<String>();
		for (MethodDeclaration md : methods) {
			String mName = md.getName().toString();
			if (mName.contains("get") || mName.contains("set"))
				mNameSet.add(md.getName().toString());
		}

		StringBuilder sb = new StringBuilder();
		for (FieldDeclaration fNode : fields) {
			String fName = getFieldName(fNode);
			char sChar = ';';
			String newFName = fName;
			if (fName.charAt(0) >= 'a' && fName.charAt(0) <= 'z') {
				sChar = (char) (fName.charAt(0) - 'a' + 'A');
				newFName = sChar + fName.substring(1);
			}
			// getter
			if (!mNameSet.contains("get" + newFName)) {
				sb.append(createGetter("get" + newFName, fName, fNode));
			}
			// setter
			if (!mNameSet.contains("set" + newFName)) {
				if (!fNode.toString().contains(" final ")) {
					sb.append(createSetter("set" + newFName, fName, fNode));
				}
			}

		}

	}

	private String getFieldName(FieldDeclaration field) {
		String frag = field.fragments().toString().replace("[", "").replace("]", "");
		if (frag.contains("="))
			frag = frag.substring(0, frag.indexOf("=")).trim();
		String[] token = frag.split(" ");
		return token[token.length - 1];
	}

	@SuppressWarnings("unchecked")
	private String createGetter(String funcName, String fName, FieldDeclaration field) {
		MethodDeclaration methodNode = node.getAST().newMethodDeclaration();
		methodNode.setName(methodNode.getAST().newSimpleName(funcName));

		ReturnStatement returnS = methodNode.getAST().newReturnStatement();
		FieldAccess fieldS = methodNode.getAST().newFieldAccess();
		fieldS.setName(methodNode.getAST().newSimpleName(fName));
		returnS.setExpression(methodNode.getAST().newFieldAccess());
		methodNode.setReturnType2(field.getType());
		methodNode.modifiers().add(Modifier.PUBLIC);
		if (field.modifiers().contains(Modifier.STATIC))
			methodNode.modifiers().add(Modifier.STATIC);

		ListRewrite bodylrw = ASTRewrite.create(node.getAST()).getListRewrite(node,
				TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		bodylrw.insertLast(methodNode, null);

		if (field.modifiers().contains("static"))
			return "public static " + field.getType() + "  " + funcName + "() { \n\t return " + fName + ";\n}\n\n";
		else
			return "public " + field.getType() + "  " + funcName + "() { \n\t return " + fName + ";\n}\n\n";

	}

	@SuppressWarnings("unchecked")
	private String createSetter(String funcName, String fName, FieldDeclaration field) {
		AST typeNode = node.getAST();
		MethodDeclaration methodNode = typeNode.newMethodDeclaration();

		SingleVariableDeclaration para = methodNode.getAST().newSingleVariableDeclaration();
		para.setType(field.getType());
		para.setName(methodNode.getAST().newSimpleName(fName));
		para.setVarargs(false);
		para.setExtraDimensions(0);

		methodNode.parameters().add(para);
		// methodNode.parameters();
		methodNode.setName(methodNode.getAST().newSimpleName(funcName));
		methodNode.modifiers().add(Modifier.PUBLIC);
		if (field.modifiers().contains(Modifier.STATIC))
			methodNode.modifiers().add(Modifier.STATIC);

		ListRewrite bodylrw = ASTRewrite.create(node.getAST()).getListRewrite(node,
				TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

		bodylrw.insertLast(methodNode, null);

		if (field.modifiers().contains("static"))
			return "public static void " + funcName + " (" + field.getType() + " " + fName + ") {\n\t  this." + fName
					+ " = " + fName + ";\n}\n\n  ";
		else
			return "public void " + funcName + " (" + field.getType() + " " + fName + ") {\n\t  this." + fName + " = "
					+ fName + ";\n}\n\n  ";
	}

	public TypeDeclaration getUpdate() {
		return node;
	}

}
