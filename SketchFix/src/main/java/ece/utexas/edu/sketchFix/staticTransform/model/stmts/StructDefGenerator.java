/**
 * @author Lisa Aug 1, 2016 StructDefAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeResolver;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeUsageRecorder;
import sketch.compiler.ast.core.Annotation;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.StructDef.TStructCreator;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;
import sketch.util.datastructures.HashmapList;

public class StructDefGenerator {
	private HashMap<String, StructDef> structDefMap = new HashMap<String, StructDef>();
	private HashMap<String, Function> methodMap = new HashMap<String, Function>();
	TypeResolver resolver;

	public StructDefGenerator(TypeUsageRecorder recorder, TypeResolver resolver) {
		this.resolver = resolver;
		HashMap<String, HashSet<String>> fields = recorder.getFieldMap();
		HashMap<String, HashSet<String>> methods = recorder.getMethodMap();

		for (String type : fields.keySet()) {
			initStructDef(type, fields.get(type), recorder.getConstructors().get(type));
		}
		for (String type : recorder.getConstructors().keySet()) {
			initStructDef(type, fields.get(type), recorder.getConstructors().get(type));
		}
		for (String type : TypeAdapter.getTypeMap().keySet()) {
			initStructDef(type, null, null);
		}
		for (String type : methods.keySet()) {
			for (String mtd : methods.get(type)) {
				initMethod(resolver.getMethodWrapper(type, mtd));
			}
		}
	}

	private void initStructDef(String typeName, HashSet<String> fields, HashMap<String, String> constructor) {
		if (typeName.contains("int") || typeName.contains("float") || typeName.contains("double")
				|| typeName.contains("[") || typeName.contains("bit") || typeName.contains("boolean")
				|| typeName.contains("char") || typeName.contains("null"))
			return;
		TStructCreator creator = new TStructCreator(AbstractASTAdapter.getContext2());
		creator.name(typeName);
		List<String> names = new ArrayList<String>();
		List<Type> types = new ArrayList<Type>();
		if (fields != null) {
			for (String field : fields) {
				names.add(field);
				Type t = TypeAdapter.getType(resolver.getFieldType(typeName, field));
				types.add(t == null ? TypePrimitive.int32type : t);
			}
		}
		// FIXME mann, I know it's buggy
		if (constructor != null) {
			for (String type : constructor.keySet()) {
				names.add(constructor.get(type));
				Type t = TypeAdapter.getType(type);
				types.add(t == null ? TypePrimitive.int32type : t);
			}
		}
		HashmapList<String, Annotation> annotations = new HashmapList<String, Annotation>();
		creator.annotations(annotations);
		creator.fields(names, types);
		StructDef def = creator.create();
		if (!structDefMap.containsKey(typeName)
				|| structDefMap.get(typeName).toString().length() < def.toString().length())
			structDefMap.put(typeName, def);
	}

	private void initMethod(MethodWrapper wrap) {
		if (wrap == null)
			return;
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(wrap.getMethodName());
		Function methodNode = creator.create();
		String paraBase = "_a";
		int count = 0;
		List<Parameter> param = new ArrayList<Parameter>();
		param.add(
				new Parameter(methodNode.getOrigin(), TypeAdapter.getType(wrap.getClassName()), (paraBase + count++)));

		for (String name : wrap.getParamList())
			param.add(new Parameter(methodNode.getOrigin(), TypeAdapter.getType(name), (paraBase + count++)));

		if (wrap.getReturnType() != null && !wrap.getReturnType().equals("void"))
			param.add(new Parameter(methodNode.getOrigin(), TypeAdapter.getType(wrap.getReturnType()),
					(paraBase + count++)));
		creator.params(param);

		List<Statement> body = new ArrayList<Statement>();
		creator.body(new StmtBlock(methodNode.getOrigin(), body));
		// TODO add repair here
		methodMap.put(wrap.getMethodName(), creator.create());
	}

	public Collection<StructDef> getStructDefMap() {
		return structDefMap.values();
	}

	public Collection<Function> getMethodMap() {
		return methodMap.values();
	}

}
