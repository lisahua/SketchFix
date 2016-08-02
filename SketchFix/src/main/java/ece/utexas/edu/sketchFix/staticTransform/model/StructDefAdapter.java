/**
 * @author Lisa Aug 1, 2016 StructDefAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.typs.Type;

public class StructDefAdapter {
	private static HashMap<String, HashSet<String>> structFieldMap = new HashMap<String, HashSet<String>>();
	private static HashMap<String, Function> methodMap = new HashMap<String, Function>();
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
	// public static void insertStruct(String type) {
	// structMap.add(type);
	// }
	public static void createStructs() {

	}

	public static void createMethods() {

	}

	public static void insertMethod(String name, Type invokerType, List<Type> typeArg) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(name);
		// creator.params(param);
		List<Parameter> param = new ArrayList<Parameter>();
		Parameter para = new Parameter(AbstractASTAdapter.getContext(), invokerType, AbstractASTAdapter.getNextName());
		param.add(para);
		for (Type ty : typeArg)
			param.add(new Parameter(AbstractASTAdapter.getContext(), ty, AbstractASTAdapter.getNextName()));
		creator.params(param);
		Function function = creator.create();
		methodMap.put(name, function);
	}

}
