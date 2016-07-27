/**
 * @author Lisa Jul 24, 2016 StaticClassVisitor.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class StaticClassVisitor extends ASTVisitor {
	private HashMap<String, FieldDeclaration> fields = new HashMap<String, FieldDeclaration>();
	private HashMap<String, MethodDeclaration> methods = new HashMap<String, MethodDeclaration>();
	boolean isInterface = false;
	Vector<StaticClassVisitor> innerClass = new Vector<StaticClassVisitor>();
	TypeDeclaration clazz = null;
	StringBuilder genFile = new StringBuilder();
	PrintWriter writer = null;

	public StaticClassVisitor(PrintWriter writer) {
		this.writer = writer;
	}

	public boolean visit(TypeDeclaration node) {
		isInterface = node.isInterface();
		if (isInterface)
			return super.visit(node);
		if (clazz == null) {
			clazz = node;
		} else {
			// current I dont add setter/getter to inner class
			StaticClassVisitor innerVisitor = new StaticClassVisitor(writer);
			node.accept(innerVisitor);
			innerClass.addElement(innerVisitor);

		}
		return super.visit(node);
	}

	public boolean visit(ImportDeclaration node) {
		writer.println(node.toString());
		writer.flush();
		return super.visit(node);
	}

	public boolean visit(PackageDeclaration node) {
		writer.println(node.toString());
		writer.flush();
		return super.visit(node);
	}

	public void endVisit(TypeDeclaration node) {
		String nodeS = node.toString();
		int id = nodeS.lastIndexOf("}");
		nodeS = nodeS.substring(0, id) + naiveRewriter().toString() + "}";
		genFile.append(nodeS);

	}

	public boolean visit(FieldDeclaration node) {
		if (isInterface)
			return super.visit(node);
		String name = getFieldName(node);
		fields.put(name, node);
		return super.visit(node);
	}

	public boolean visit(MethodDeclaration node) {
		if (isInterface)
			return super.visit(node);
		String name = node.getName().toString();
		if (name.contains("get") || name.contains("set"))
			methods.put(name, node);
		return super.visit(node);
	}

	private StringBuilder naiveRewriter() {
		for (StaticClassVisitor innerVisitor : innerClass) {
			for (String s : innerVisitor.getFields().keySet()) {
				fields.remove(s);
			}
			for (String s : innerVisitor.getMethods().keySet()) {
				methods.remove(s);
			}
		}

		StringBuilder sb = new StringBuilder();
		for (String fName : fields.keySet()) {
			FieldDeclaration fNode = fields.get(fName);
			char sChar = ';';
			String newFName = fName;
			if (fName.charAt(0) >= 'a' && fName.charAt(0) <= 'z') {
				sChar = (char) (fName.charAt(0) - 'a' + 'A');
				newFName = sChar + fName.substring(1);
			}
			// getter
			if (!methods.containsKey("get" + newFName)) {
				sb.append(createGetter("get" + newFName, fName, fNode));
			}
			// setter
			if (!methods.containsKey("set" + newFName)) {
				if (!fNode.toString().contains(" final ")) {
					sb.append(createSetter("set" + newFName, fName, fNode));
				}
			}

		}
		return sb;
	}

	private String createGetter(String funcName, String fName, FieldDeclaration field) {

		return "public " + field.getType() + "  " + funcName + "() { \n\t return " + fName + ";\n}\n\n";
	}

	private String createSetter(String funcName, String fName, FieldDeclaration field) {

		return "public void " + funcName + " (" + field.getType() + " " + fName + ") {\n\t  this." + fName + " = "
				+ fName + ";\n}\n\n  ";
	}

	private String getFieldName(FieldDeclaration field) {
		String frag = field.fragments().toString().replace("[", "").replace("]", "");
		if (frag.contains("="))
			frag = frag.substring(0, frag.indexOf("=")).trim();
		String[] token = frag.split(" ");
		return token[token.length - 1];
	}

	public HashMap<String, FieldDeclaration> getFields() {
		return fields;
	}

	public HashMap<String, MethodDeclaration> getMethods() {
		return methods;
	}

	public StringBuilder getNewFile() {
		return genFile;
	}
}
