/**
 * @author Lisa Aug 1, 2016 MethodDeclarationAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StatementAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StmtStateMapper;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeResolver;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FcnType;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.ExprNamedParam;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;

public class MethodDeclarationAdapter extends AbstractASTAdapter {
	private TypeDeclaration clazz;
	private HashMap<String, Type> varType = new HashMap<String, Type>();
	private StatementAdapter stmtAdapter;
	private FENode methodNode;
	private Type rtnType;
	private TypeResolver typeResolver;

	private boolean harness = false;
	// private ExprNew newExcp = null;
	private LinePyGenerator utility;
	private StmtStateMapper stateMapper = null;
	private OverloadHandler overloadHandler = new OverloadHandler();

	public MethodDeclarationAdapter(CompilationUnit cu, MethodData method, LinePyGenerator utility) {
		this.utility = utility;
		this.clazz = (TypeDeclaration) cu.types().get(0);
		// this.fields = clazz.getFields();
		typeResolver = new TypeResolver(cu.imports(), clazz, method.getBaseDirs());
		stmtAdapter = new StatementAdapter(this);
		stateMapper = new StmtStateMapper(utility.getTrace(), method.getTouchLinesList(), method.getBaseDirs());
	}

	public void setHarness(boolean har) {
		harness = har;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object transform(ASTNode node) {
		MethodDeclaration method = (MethodDeclaration) node;

		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		List<SingleVariableDeclaration> parameters = method.parameters();
		String name = method.getName().toString();
		for (SingleVariableDeclaration para : parameters)
			name += "_" + para.getType().toString();
		creator.name(name);

		overloadHandler.process(name, method);

		List<Parameter> param = generateParam(method);
		creator.params(param);
		List<Statement> body = new ArrayList<Statement>();
		List<org.eclipse.jdt.core.dom.Statement> stmts = ((Block) method.getBody()).statements();

		methodNode = creator.create();
		for (org.eclipse.jdt.core.dom.Statement stmt : stmts) {
			// for (ASTLinePy line : astLines) {
			// org.eclipse.jdt.core.dom.Statement stmt = line.getStatement();
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
		if (harness)
			creator.type(FcnType.Harness);
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
		// this object
		Parameter thisParam = new Parameter(getMethodContext(), (Type) TypeAdapter.getType(clazz.getName().toString()),
				AbstractASTAdapter.thisClass);
		param.add(thisParam);

		for (int i = 0; i < parameters.size(); i++) {
			Parameter p = new Parameter(getMethodContext(), (Type) TypeAdapter.getType(overloadHandler.convertParam(i)),
					parameters.get(i).getName().toString());
			varType.put(p.getName(), p.getType());
			param.add(p);
		}

		// exception object
		// Parameter excpParam = new Parameter(getMethodContext(),
		// AbstractASTAdapter.excepType,
		// AbstractASTAdapter.excepName);
		// param.add(excpParam);
		// varType.put(AbstractASTAdapter.excepName,
		// AbstractASTAdapter.excepType);

		// return value;
//		Parameter excpParam = new Parameter(getMethodContext(), TypePrimitive.bittype, AbstractASTAdapter.excepName);
//		AbstractASTAdapter.excepName += AbstractASTAdapter.excepName + 1;
//		param.add(excpParam);
//		varType.put(excpParam.getName(), excpParam.getType());
		rtnType = (Type) TypeAdapter.getType(returnType.toString());
		if (rtnType != null) {
			Parameter rtnParam = new Parameter(getMethodContext(), rtnType, AbstractASTAdapter.returnObj);
			param.add(rtnParam);
			varType.put(rtnParam.getName(), rtnParam.getType());
		}
		return param;
	}

	public void insertVarDecl(String name, Type type) {
		varType.put(name, type);
	}

	public Type getFieldTypeOf(String type, String field) {
		String fType = typeResolver.getFieldType(type, field);
		return TypeAdapter.getType(fType);
	}

	public Type getVarType(String var) {
		if (varType.containsKey(var))
			return varType.get(var);

		else if (var.equals(AbstractASTAdapter.thisClass))
			return TypeAdapter.getType(clazz.getName().toString());
		else if (var.equals(AbstractASTAdapter.returnObj))
			return rtnType;
		return null;
		// String fType = typeResolver.getFieldType(clazz.getName().toString(),
		// var);
		// return TypeAdapter.getType(fType);
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

	public String getCurrentClassType() {
		return clazz.getName().toString();
	}

	public TypeResolver getTypeResolver() {
		return typeResolver;
	}

	public void updateParaType(String classType, String method, int id, String type) {
		typeResolver.updateParaType(classType, method, id, type);
	}

	private boolean isTestMethod(String name) {
		return harness;
	}

	// public ExprNew getNewException() {
	// if (newExcp == null)
	// newExcp = new ExprNew(getMethodContext(), AbstractASTAdapter.excepType,
	// new ArrayList<ExprNamedParam>(),
	// false);
	// return newExcp;
	// }

	public LinePyGenerator getLinePyGenerator() {
		return utility;
	}

	public String getVarOfType(Type type) {
		for (String name : varType.keySet()) {
			if (varType.get(name).toString().equals(type.toString()))
				return name;
		}
		return null;
	}

	public StmtStateMapper getStateMapper() {
		return stateMapper;
	}

	public OverloadHandler getOverloadHandler() {
		return overloadHandler;
	}

	public void setOverloadHandler(OverloadHandler handler) {
		this.overloadHandler = handler;
	}

	public MethodWrapper validateParams(String type, List<String> paramTypes) {

		return typeResolver.validateParams(type, paramTypes);
	}
}
