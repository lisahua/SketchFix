/**
 * @author Lisa Aug 1, 2016 MethodDeclarationAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StatementAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeResolver;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeUsageRecorder;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.typs.Type;

public class MethodDeclarationAdapter extends AbstractASTAdapter {
	private TypeDeclaration clazz;
	// private FieldDeclaration[] fields;
	private List<ASTLinePy> astLines;
	// private HashMap<String, Type> fieldType = new HashMap<String, Type>();
	private HashMap<String, Type> varType = new HashMap<String, Type>();
	// private HashMap<String, Type> usedFieldType = new HashMap<String,
	// Type>();
	private StatementAdapter stmtAdapter;
	private FENode methodNode;
	private Type rtnType;
	private TypeResolver typeResolver;
	private TypeUsageRecorder useRecorder = new TypeUsageRecorder();

	@SuppressWarnings("unchecked")
	public MethodDeclarationAdapter(CompilationUnit cu, List<ASTLinePy> astLines) {
		// TypeDeclaration clazz, FieldDeclaration[] fields,
		this.clazz = (TypeDeclaration) cu.types().get(0);
		// this.fields = clazz.getFields();
		this.astLines = astLines;
		typeResolver = new TypeResolver(cu.imports(),clazz);
		stmtAdapter = new StatementAdapter(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object transform(ASTNode node) {
		MethodDeclaration method = (MethodDeclaration) node;

		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(method.getName().toString());
		methodNode = creator.create();

		List<Parameter> param = generateParam(method);
		creator.params(param);

		List<Statement> body = new ArrayList<Statement>();
		for (ASTLinePy line : astLines) {
			org.eclipse.jdt.core.dom.Statement stmt = line.getStatement();
			Object obj = stmtAdapter.transform(stmt);
			if (obj == null)
				continue;
			if (obj instanceof Statement)
				body.add((Statement) obj);
			else
				body.addAll((List<Statement>) obj);
		}

		StmtBlock block = new StmtBlock(getMethodContext(), body);
		creator.body(block);

		// TODO add repair here
		Function function = creator.create();

		return function;
	}

	@SuppressWarnings("unchecked")
	private List<Parameter> generateParam(MethodDeclaration method) {
		org.eclipse.jdt.core.dom.Type returnType = method.getReturnType2();
		List<SingleVariableDeclaration> parameters = method.parameters();

		List<Parameter> param = new ArrayList<Parameter>();
		if (isTestMethod(method.getName().toString()))
			return param;

		Parameter thisParam = new Parameter(getMethodContext(), (Type) TypeAdapter.getType(clazz.getName().toString()),
				AbstractASTAdapter.thisClass);
		param.add(thisParam);
		rtnType = (Type) TypeAdapter.getType(returnType.toString());
		if (rtnType != null) {
			Parameter rtnParam = new Parameter(getMethodContext(), rtnType, AbstractASTAdapter.returnObj);
			param.add(rtnParam);
			varType.put(rtnParam.getName(), rtnParam.getType());
		}

		for (SingleVariableDeclaration para : parameters) {
			Parameter p = new Parameter(getMethodContext(), (Type) TypeAdapter.getType(para.getType().toString()),
					para.getName().toString());
			varType.put(p.getName(), p.getType());
			param.add(p);
		}
		return param;
	}

	// @SuppressWarnings("unchecked")
	// private void parseField() {
	// for (FieldDeclaration field : fields) {
	// org.eclipse.jdt.core.dom.Type jType = field.getType();
	// Type sType = (Type) TypeAdapter.getInstance().recordField(jType);
	// List<VariableDeclarationFragment> list = field.fragments();
	// for (VariableDeclarationFragment frag : list) {
	// fieldType.put(frag.getName().getIdentifier(), sType);
	// }
	// }
	// }

	public void insertVarDecl(String name, Type type) {
		varType.put(name, type);
	}

	// public void insertUsedField(String name, Type type) {
	// usedFieldType.put(name, type);
	// }

	public Type getFieldTypeOf(String type, String field) {
		String fType = typeResolver.getFieldType(type, field);
		String name = clazz.getName().toString();
		if (type.equals(name)) {
			useRecorder.insertField(type, field);
			return TypeAdapter.getType(fType);
		} else {
			// TODO recursive check type
		}
		return null;
	}

	public Type getVarType(String var) {
		if (varType.containsKey(var))
			return varType.get(var);

		else if (var.equals(AbstractASTAdapter.thisClass))
			return TypeAdapter.getType(clazz.getName().toString());
		else if (var.equals(AbstractASTAdapter.returnObj))
			return rtnType;

		String fType = typeResolver.getFieldType(clazz.getName().toString(), var);
		return TypeAdapter.getType(fType);
	}

	public Type getMethodReturnType(String type, String method) {
		String rtnType = typeResolver.getMethodReturnType(type, method);
		return TypeAdapter.getType(rtnType);
	}

	public MethodWrapper getMethodModel(String type, String method) {
		return typeResolver.getMethodWrapper(type, method);
	}

	public FENode getMethodContext() {
		return methodNode;
	}

	public void insertUseField(String type, String field) {
		useRecorder.insertField(type, field);
	}

	public void insertUseMethod(String type, String method) {
		useRecorder.insertMethod(type, method);
	}

	public String getCurrentClassType() {
		return clazz.getName().toString();
	}

	public TypeUsageRecorder getUseRecorder() {
		return useRecorder;
	}

	public TypeResolver getTypeResolver() {
		return typeResolver;
	}

	public void updateParaType(String classType, String method, int id, String type) {
		typeResolver.updateParaType(classType, method, id, type);
	}

	private boolean isTestMethod(String name) {
		return name.toLowerCase().contains("test");
	}
}
