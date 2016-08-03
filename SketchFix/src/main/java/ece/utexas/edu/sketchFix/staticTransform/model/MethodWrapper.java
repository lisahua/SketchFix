/**
 * @author Lisa Aug 3, 2016 MethodWrapper.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodWrapper {

	String className;
	String methodName;
	String returnType;
	HashMap<String, String> paramType = new HashMap<String, String>();

	public MethodWrapper(MethodDeclaration methodNode) {
		
	}
	
	public void addParam(String name, String type) {
		paramType.put(name, type);
	}

	public String getParamType(String name) {
		return paramType.get(name);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	
}
