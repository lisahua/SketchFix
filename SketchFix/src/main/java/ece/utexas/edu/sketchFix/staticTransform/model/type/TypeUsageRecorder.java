/**
 * @author Lisa Aug 4, 2016 TypeUsageRecorder.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.model.ConstructorWrapper;

public class TypeUsageRecorder {
	private HashMap<String, HashSet<String>> fieldMap = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> methodMap = new HashMap<String, HashSet<String>>();
	private List<ConstructorWrapper> constructors = new ArrayList<ConstructorWrapper>();

	public void insertField(String type, String field) {
		HashSet<String> fields = (fieldMap.containsKey(type)) ? fieldMap.get(type) : new HashSet<String>();
		fields.add(field);
		fieldMap.put(type, fields);
		// System.out.println("insert field " + type + "," + fields);
	}

	public void insertMethod(String type, String method) {
		HashSet<String> methods = (methodMap.containsKey(type)) ? methodMap.get(type) : new HashSet<String>();
		methods.add(method);
		methodMap.put(type, methods);
		// System.out.println("insert method " + type + "," + methods);
	}

	public HashMap<String, HashSet<String>> getFieldMap() {
		return fieldMap;
	}

	public HashMap<String, HashSet<String>> getMethodMap() {
		return methodMap;
	}

	public ConstructorWrapper insertUseConstructor(String type, List<String> paramTypes) {
		type = type.replace(".", "");
		// HashSet<FieldWrapper> constructs = new HashSet<FieldWrapper>();
		int lastID = 0;
		for (ConstructorWrapper wrap : constructors) {
			if (wrap.getClassName().equals(type)) {
				lastID = wrap.getLastID();
				if (wrap.matchConstructor(paramTypes))
					return wrap;
			}
		}
		ConstructorWrapper newConst = new ConstructorWrapper(type, paramTypes, lastID);
		constructors.add(newConst);
		return newConst;
	}

	public HashMap<String, HashMap<String, String>> getConstructors() {
		HashMap<String, HashMap<String, String>> constructMap = new HashMap<String, HashMap<String, String>>();
		for (ConstructorWrapper wrap : constructors) {
			String className = wrap.getClassName();
			HashMap<String, String> nameTypeMap = (constructMap.containsKey(className)) ? constructMap.get(className)
					: new HashMap<String, String>();
			for (int i = 0; i < wrap.getNames().size(); i++) {
				nameTypeMap.put(wrap.getNames().get(i), wrap.getTypes().get(i));
			}
			constructMap.put(className, nameTypeMap);
		}
		return constructMap;
	}

}
