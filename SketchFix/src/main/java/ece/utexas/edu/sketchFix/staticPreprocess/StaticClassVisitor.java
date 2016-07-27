/**
 * @author Lisa Jul 24, 2016 StaticClassVisitor.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

public class StaticClassVisitor extends ASTVisitor {
	private HashMap<String, FieldDeclaration> fields = new HashMap<String, FieldDeclaration>();
	private HashMap<String, MethodDeclaration> methods = new HashMap<String, MethodDeclaration>();

	public boolean visit(FieldDeclaration node) {
		String name = getFieldName(node);
		fields.put(name, node);
		return super.visit(node);
	}

	public boolean visit(MethodDeclaration node) {
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
			String newFName = "";
			if (fName.charAt(0) >= 'a' && fName.charAt(0) <= 'z') {
				sChar = (char) (fName.charAt(0) - 'a' + 'A');
				newFName = sChar + fName.substring(1);
			}
			// getter
			if (!methods.containsKey("get" + newFName)) {
				sb.append(createGetter("get" + newFName, fNode));
			}
			// setter
			if (!methods.containsKey("set" + newFName)) {
				if (!fNode.modifiers().contains(Modifier.FINAL)) {
					sb.append(createSetter("set" + newFName, fNode));
				}
			}

		}
		return sb;
	}

	private String createGetter(String funcName, FieldDeclaration field) {
		@SuppressWarnings("rawtypes")
		List frag = field.fragments();
		int id = frag.indexOf("=");
		String type = "";
		String name = "";
		if (id > 2) {
			type = (String) frag.get(id - 2);
			name = (String) frag.get(id - 1);
		} else {
			type = ((String) frag.get(frag.size() - 2));
			name = ((String) frag.get(frag.size() - 1)).replace(";", "");
		}

		return "public " + type + funcName + "() { \n\t return " + name + ";\n}";
	}

	private String createSetter(String funcName, FieldDeclaration field) {
		@SuppressWarnings("rawtypes")
		List frag = field.fragments();
		int id = frag.indexOf("=");
		String type = "";
		String name = "";
		if (id > 2) {
			type = (String) frag.get(id - 2);
			name = (String) frag.get(id - 1);
		} else {
			type = ((String) frag.get(frag.size() - 2));
			name = ((String) frag.get(frag.size() - 1)).replace(";", "");
		}

		return "public void " + funcName + " (" + type + " o) {\n\t  this." + name + " = o;\n}  ";
	}

	private String getFieldName(FieldDeclaration field) {
		@SuppressWarnings("rawtypes")
		List frag = field.fragments();
		int id = frag.indexOf("=");
		String name = "";
		if (id > 1)
			name = (String) frag.get(id - 1);
		else
			name = ((String) frag.get(frag.size() - 1)).replace(";", "");
		return name;
	}
}
