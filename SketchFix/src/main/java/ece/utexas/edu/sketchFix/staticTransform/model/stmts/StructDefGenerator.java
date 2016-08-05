/**
 * @author Lisa Aug 1, 2016 StructDefAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.ast.core.Annotation;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.StructDef.TStructCreator;
import sketch.compiler.ast.core.typs.Type;
import sketch.util.datastructures.HashmapList;

public class StructDefGenerator {
	private static HashMap<String, StructDef> structDefMap = new HashMap<String, StructDef>();
	private static HashMap<String, Function> methodMap = new HashMap<String, Function>();
	private static HashSet<String> structMap = new HashSet<String>();

	public static void insertStruct(String type) {
		structMap.add(type);
	}

	public static List<StructDef> createStructs() {
		List<StructDef> structs = new ArrayList<StructDef>();
		TStructCreator creator = new TStructCreator(AbstractASTAdapter.getContext2());
		for (String sName : structMap) {
			if (structDefMap.containsKey(sName)) {
				structs.add(structDefMap.get(sName));
			} else {
				creator.name(sName);
				List<String> names = new ArrayList<String>();
				List<Type> types = new ArrayList<Type>();
				HashmapList<String, Annotation> annotations = new HashmapList<String, Annotation>();
				creator.annotations(annotations);
				creator.fields(names, types);
				structs.add(creator.create());
			}
		}
		return structs;
	}

	public static List<Function> createMethods(List<MethodData> locations) {
		List<Function> methods = new ArrayList<Function>();
		for (Function func : methodMap.values())
			methods.add(func);
		return methods;
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
		List<Statement> body = new ArrayList<Statement>();
		StmtBlock block = new StmtBlock(AbstractASTAdapter.getContext(), body);
		creator.body(block);
		Function function = creator.create();
		methodMap.put(name, function);
	}

	public static void insertParamterToMethod(String name, Parameter para) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(name);
		// creator.params(param);
		List<Parameter> param = methodMap.get(name).getParams();
		List<Parameter> newParam = new ArrayList<Parameter>();
		newParam.addAll(param);
		newParam.add(para);
		creator.params(newParam);
		List<Statement> body = new ArrayList<Statement>();
		StmtBlock block = new StmtBlock(AbstractASTAdapter.getContext(), body);
		creator.body(block);
		Function function = creator.create();
		methodMap.put(name, function);
	}

	public static Function getMethod(String name) {
		return methodMap.get(name);
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
		HashmapList<String, Annotation> annotations = new HashmapList<String, Annotation>();
		names.add(name);
		types.add(fieldT);
		creator.fields(names, types);
		creator.annotations(annotations);
		structDefMap.put(type, creator.create());
	}
}
