/**
 * @author Lisa Aug 1, 2016 StructDefAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeResolver;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeUsageRecorder;
import sketch.compiler.ast.core.Annotation;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.StructDef.TStructCreator;
import sketch.compiler.ast.core.typs.Type;
import sketch.util.datastructures.HashmapList;

public class StructDefGenerator {
	private HashSet<StructDef> structDefMap = new HashSet<StructDef>();
	private HashSet<Function> methodMap = new HashSet<Function>();
	TypeResolver resolver;

	public StructDefGenerator(TypeUsageRecorder recorder, TypeResolver resolver) {
		this.resolver = resolver;
		HashMap<String, HashSet<String>> fields = recorder.getFieldMap();
		HashMap<String, HashSet<String>> methods = recorder.getMethodMap();

		for (String type : fields.keySet()) {
			initStructDef(type, fields.get(type));
		}
		for (String type : TypeAdapter.getTypeMap().keySet()) {
			initStructDef(type, null);
		}
		for (String type : methods.keySet()) {
			for (String mtd : methods.get(type)) {
				initMethod(resolver.getMethodWrapper(type, mtd));
			}
		}
	}



	private void initStructDef(String typeName, HashSet<String> fields) {
		TStructCreator creator = new TStructCreator(AbstractASTAdapter.getContext2());
		creator.name(typeName);
		List<String> names = new ArrayList<String>();
		List<Type> types = new ArrayList<Type>();
		if (fields != null) {
			for (String field : fields) {
				names.add(field);
				types.add(TypeAdapter.getType(resolver.getFieldType(typeName, field)));
			}
		}
		HashmapList<String, Annotation> annotations = new HashmapList<String, Annotation>();
		creator.annotations(annotations);
		creator.fields(names, types);
		structDefMap.add(creator.create());
	}

	private void initMethod(MethodWrapper wrap) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(wrap.getMethodName());
		Function methodNode = creator.create();
		String paraBase = "_a";
		int count = 0;
		List<Parameter> param = new ArrayList<Parameter>();
		param.add(
				new Parameter(methodNode.getOrigin(), TypeAdapter.getType(wrap.getClassName()), (paraBase + count++)));
		for (String name : wrap.getParamType().keySet())
			param.add(new Parameter(methodNode.getOrigin(), TypeAdapter.getType(wrap.getParamType(name)),
					(paraBase + count++)));
		if (!wrap.getReturnType().equals("void"))
			param.add(new Parameter(methodNode.getOrigin(), TypeAdapter.getType(wrap.getReturnType()),
					(paraBase + count++)));
		creator.params(param);

		List<Statement> body = new ArrayList<Statement>();
		creator.body(new StmtBlock(methodNode.getOrigin(), body));
		// TODO add repair here
		methodMap.add(creator.create());
	}



	public HashSet<StructDef> getStructDefMap() {
		return structDefMap;
	}



	public HashSet<Function> getMethodMap() {
		return methodMap;
	}

}
