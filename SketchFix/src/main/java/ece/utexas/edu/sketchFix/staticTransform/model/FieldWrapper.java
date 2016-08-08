/**
 * @author Lisa Aug 3, 2016 MethodWrapper.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class FieldWrapper {

	String className;
	HashMap<String, String> fieldTypeMap = new HashMap<String, String>();

	public FieldWrapper(TypeDeclaration node) {
		className = node.getName().toString();
		FieldDeclaration[] fields = node.getFields();
		for (FieldDeclaration f : fields) {
			List<VariableDeclaration> frag = f.fragments();
			for (VariableDeclaration var : frag) {
				fieldTypeMap.put(var.getName().toString(), f.getType().toString());
			}
		}
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getFieldType(String name) {
		return fieldTypeMap.get(name);
	}

	public void addFieldType(String fieldName, String fType) {
		fieldTypeMap.put(fieldName, fType);
	}

}
