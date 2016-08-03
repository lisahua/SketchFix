/**
 * @author Lisa Aug 3, 2016 MethodWrapper.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class FieldWrapper {

	String className;
	HashMap<String, String> fieldTypeMap = new HashMap<String, String>();

	public FieldWrapper(TypeDeclaration methodNode) {

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
