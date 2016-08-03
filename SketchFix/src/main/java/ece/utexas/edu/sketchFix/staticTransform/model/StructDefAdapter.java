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
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.StructDef.TStructCreator;
import sketch.compiler.ast.core.typs.Type;

public class StructDefAdapter {
	private static HashMap<String, StructDef> structDefMap = new HashMap<String, StructDef>();
	private static HashMap<String, Function> methodMap = new HashMap<String, Function>();
	private static HashSet<String> structMap = new HashSet<String>();

	// public static void insertField(String type, String field) {
	// HashSet<String> fields = (structFieldMap.containsKey(type)) ?
	// structFieldMap.get(type) : new HashSet<String>();
	// fields.add(field);
	// structFieldMap.put(type, fields);
	// }

	public static void insertStruct(String type) {
		structMap.add(type);
	}

	public static void createStructs() {

	}

	public static void createMethods() {

	}

	/**
	 * Only register when it is used
	 * 
	 * @param type
	 * @param field
	 */
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

	/**
	 * Only register when it is used
	 * 
	 * @param type
	 * @param field
	 */
	public static void insertField(String type, String name, Type fieldT) {
		TStructCreator creator;
		if (structDefMap.containsKey(type))
			creator = new TStructCreator(structDefMap.get(type));
		else
			creator = new TStructCreator(AbstractASTAdapter.getContext2());
		creator.name(type);
		List<String> names = new ArrayList<String>();
		List<Type> types = new ArrayList<Type>();
		names.add(name);
		creator.fields(names, types);
		structDefMap.put(type, creator.create());
	}
}
