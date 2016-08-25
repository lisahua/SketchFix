/**
 * @author Lisa Aug 4, 2016 TypeUsageRecorder.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.type;

import java.util.HashMap;
import java.util.HashSet;

public class TypeUsageRecorder {
	private HashMap<String, HashSet<String>> fieldMap = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> methodMap = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashMap<String, String>> constructors = new HashMap<String, HashMap<String, String>>();

	public void insertField(String type, String field) {
		HashSet<String> fields = (fieldMap.containsKey(type)) ? fieldMap.get(type) : new HashSet<String>();
		fields.add(field);
		fieldMap.put(type, fields);
//		System.out.println("insert field " + type + "," + fields);
	}

	public void insertMethod(String type, String method) {
		HashSet<String> methods = (methodMap.containsKey(type)) ? methodMap.get(type) : new HashSet<String>();
		methods.add(method);
		methodMap.put(type, methods);
//		System.out.println("insert method " + type + "," + methods);
	}

	public HashMap<String, HashSet<String>> getFieldMap() {
		return fieldMap;
	}

	public HashMap<String, HashSet<String>> getMethodMap() {
		return methodMap;
	}

	public String insertUseConstructor(String type, String varType) {
		type = type.replace(".", "");
		
		HashMap<String, String> typeMatch = (constructors.containsKey(type)) ? constructors.get(type)
				: new HashMap<String, String>();
		if (typeMatch.containsKey(varType)) {
			return typeMatch.get(varType);
		}
		
		String varName =  "_constr" + (typeMatch.size() + 1);
		typeMatch.put(varType, varName);
		constructors.put(type, typeMatch);
		return varName;
	}

	public HashMap<String, HashMap<String, String>> getConstructors() {
		return constructors;
	}
	
	

}
