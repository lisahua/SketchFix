/**
 * @author Lisa Aug 3, 2016 MethodWrapper.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;

public class MethodWrapper {

	String className;
	String methodName;
	String returnType;
	HashMap<String, String> paramType = new HashMap<String, String>();
	List<String> paramList = new ArrayList<String>();

	public MethodWrapper(String string, MethodDeclaration methodNode) {
		className = string;
		methodName = methodNode.getName().toString();
		returnType = (methodNode.getReturnType2() == null) ? "void" : methodNode.getReturnType2().toString();
		@SuppressWarnings("unchecked")
		List<SingleVariableDeclaration> params = methodNode.parameters();
		for (SingleVariableDeclaration para : params) {
			paramType.put(para.getName().toString(), para.getType().toString());
			methodName += "_" + para.getType().toString();
			paramList.add(para.getType().toString());
		}
	}

	public MethodWrapper(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}

	public void updateParam(int id, String type) {
		// if (paramList.size() > 0)
		// return;
		if (type.contains("null")  )
			return;
		if (id >= paramList.size())
			paramList.add(type);
		else if (!paramList.get(id).equals(type)) {
			paramList.add(id, type);
			paramList.remove(id + 1);
		}

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

	public HashMap<String, String> getParamType() {
		return paramType;
	}

	public List<String> getParamList() {
		return paramList;
	}

}
