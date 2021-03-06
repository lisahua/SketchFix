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
		HashMap<String, HashMap<String, String>> constructor = recorder.getConstructors();

		for (String type : TypeAdapter.getTypeMap().keySet()) {
			initStructDef(type, null, null);
		}
		for (String type : fields.keySet()) {
			initStructDef(type, fields.get(type), constructor.get(type));
		}
		for (String type : constructor.keySet()) {
			initStructDef(type, fields.get(type), constructor.get(type));
		}
		for (String type : methods.keySet()) {
			for (String mtd : methods.get(type)) {
				initMethod(resolver.getMethodWrapper(type, mtd));
			}
		}
	}

	private void initStructDef(String typeName, HashSet<String> fields, HashMap<String, String> constructor) {
		if (typeName.equals("int") || typeName.equals("float") || typeName.equals("double")
				|| typeName.contains("[") || typeName.equals("bit") || typeName.equals("boolean")
				|| typeName.equals("char") || typeName.contains("null"))
			return;
		TStructCreator creator = new TStructCreator(AbstractASTAdapter.getContext2());
		creator.name(typeName);
		List<String> names = new ArrayList<String>();
		List<Type> types = new ArrayList<Type>();
		StructDef oldDef = structDefMap.get(typeName);
		if (oldDef != null) {
			for (String key : oldDef.getFieldTypMap().keySet()) {
				names.add(key);
				types.add(oldDef.getFieldTypMap().get(key));
			}
		}
		if (fields != null) {
			for (String field : fields) {
				if (names.contains(field))
					continue;
				names.add(field);
				Type t = TypeAdapter.getType(resolver.getFieldType(typeName, field));
				types.add(t == null ? TypePrimitive.int32type : t);
			}
		}
		// FIXME mann, I know it's buggy
		if (constructor != null) {
			for (String name : constructor.keySet()) {
				if (names.contains(name))
					continue;
				names.add(name);
				Type t = TypeAdapter.getType(constructor.get(name));
				types.add(t == null ? TypePrimitive.int32type : t);
			}
		}

		HashmapList<String, Annotation> annotations = new HashmapList<String, Annotation>();
		creator.annotations(annotations);
		creator.fields(names, types);
		StructDef def = creator.create();
		if (!structDefMap.containsKey(typeName)
				|| structDefMap.get(typeName).getNumFields() < def.getNumFields())
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
