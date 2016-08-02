/**
 * @author Lisa Aug 1, 2016 StructDefAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.HashMap;
import java.util.HashSet;

public class StructDefAdapter {
	private static HashMap<String, HashSet<String>> structFieldMap = new HashMap<String, HashSet<String>>();
	private static HashMap<String, HashSet<String>> methodMap = new HashMap<String, HashSet<String>>();
	private static HashSet<String> structMap = new HashSet<String>();

	/**
	 * Only register when it is used
	 * 
	 * @param type
	 * @param field
	 */
	public static void insertField(String type, String field) {
		HashSet<String> fields = (structFieldMap.containsKey(type)) ? structFieldMap.get(type) : new HashSet<String>();
		fields.add(field);
		structFieldMap.put(type, fields);
	}

	/**
	 * Only register when it is used
	 * 
	 * @param type
	 * @param field
	 */
	public static void insertMethod(String field, String type) {
		HashSet<String> methods = (methodMap.containsKey(type)) ? methodMap.get(type)
				: new HashSet<String>();
		methods.add(field);
		methodMap.put(type, methods);
	}

	/**
	 * Only register when it is used
	 * 
	 * @param type
	 * @param field
	 */
	public static void insertStruct(String type) {
		structMap.add(type);
	}
	

}
