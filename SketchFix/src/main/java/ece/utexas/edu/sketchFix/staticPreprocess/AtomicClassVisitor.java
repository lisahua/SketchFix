/**
 * @author Lisa Jul 24, 2016 StaticMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class AtomicClassVisitor extends ASTVisitor {
	private HashMap<String, FieldDeclaration> fields = new HashMap<String, FieldDeclaration>();
	private HashMap<String, MethodDeclaration> methods = new HashMap<String, MethodDeclaration>();
	boolean isInterface = false;
	
	
	public boolean visit(TypeDeclaration node) {
		isInterface = node.isInterface();
		return super.visit(node);
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

	public StringBuilder naiveRewriter() {
		

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

}
