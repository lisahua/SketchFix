/**
 * @author Lisa Jul 24, 2016 StaticClassVisitor.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class StaticClassVisitor extends ASTVisitor {
	HashMap<String, PreprocessModel> models = new HashMap<String, PreprocessModel>();
	PrintWriter writer = null;
	HashSet<TypeDeclaration> inners = new HashSet<TypeDeclaration>();
	StringBuilder plain = null;

	public StaticClassVisitor(PrintWriter writer, StringBuilder plain) {
		this.writer = writer;
		this.plain = plain;
	}

	public boolean visit(PackageDeclaration node) {
		writer.print(node);
		return super.visit(node);
	}

	public boolean visit(ImportDeclaration node) {
		writer.print(node);
		return super.visit(node);
	}

	public boolean visit(TypeDeclaration node) {
		// FIXME test it
	
		
		if (node.isInterface()) {
			return super.visit(node);
		}
		if (!(node.getParent() instanceof CompilationUnit)) {
			inners.add(node);
		}
		String name = node.getName().toString();
		PreprocessModel model = new PreprocessModel(name, naiveRewriter(node), new StringBuilder(node.toString()),plain);
		models.put(name, model);
		return super.visit(node);
	}


	private StringBuilder naiveRewriter(TypeDeclaration node) {
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
		return sb;
	}

	private String getFieldName(FieldDeclaration field) {
		String frag = field.fragments().toString().replace("[", "").replace("]", "");
		if (frag.contains("="))
			frag = frag.substring(0, frag.indexOf("=")).trim();
		String[] token = frag.split(" ");
		return token[token.length - 1];
	}

	private String createGetter(String funcName, String fName, FieldDeclaration field) {
if (field.modifiers().contains("static"))
		return "public static " + field.getType() + "  " + funcName + "() { \n\t return " + fName + ";\n}\n\n";
else 
	return "public " + field.getType() + "  " + funcName + "() { \n\t return " + fName + ";\n}\n\n";

	
	}

	private String createSetter(String funcName, String fName, FieldDeclaration field) {
		if (field.modifiers().contains("static"))
		return "public static void " + funcName + " (" + field.getType() + " " + fName + ") {\n\t  this." + fName + " = "
				+ fName + ";\n}\n\n  ";
		else 
		return "public void " + funcName + " (" + field.getType() + " " + fName + ") {\n\t  this." + fName + " = "
		+ fName + ";\n}\n\n  ";
	}

	public void removeInnerClass() {
		HashSet<String> iClass = new HashSet<String>();
		for (String cName : models.keySet()) {
			for (String c2 : models.keySet()) {
				if (cName.equals(c2))
					continue;
				if (models.get(cName).removeInner(models.get(c2))) {
					iClass.add(c2);
				}
			}
		}
		for (String cName : models.keySet()) {
			if (!iClass.contains(cName)) {
				writer.print(models.get(cName).node);
			}
		}
	}
private void createGetter() {
	
}
}
