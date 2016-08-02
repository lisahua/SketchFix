/**
 * @author Lisa Aug 1, 2016 MethodDeclarationAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.typs.Type;

public class MethodDeclarationAdapter extends AbstractASTAdapter {
	private TypeDeclaration clazz;
	private FieldDeclaration[] fields;
	private List<ASTLinePy> astLines;
	private HashMap<String, Type> fieldType = new HashMap<String, Type>();
	private HashMap<String, Type> varType = new HashMap<String, Type>();
	private HashMap<String, Type> usedFieldType = new HashMap<String, Type>();
	private StatementAdapter stmtAdapter;
	private FENode methodNode;
	private Type rtnType;

	public MethodDeclarationAdapter(TypeDeclaration clazz, FieldDeclaration[] fields, List<ASTLinePy> astLines) {
		this.clazz = clazz;
		this.fields = fields;
		this.astLines = astLines;
		parseField();
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

		Parameter thisParam = new Parameter(getMethodContext(), (Type) TypeAdapter.getInstance().transform(clazz),
				AbstractASTAdapter.thisClass);
		rtnType = (Type) TypeAdapter.getInstance().transform(returnType);
		Parameter rtnParam = new Parameter(getMethodContext(), rtnType, AbstractASTAdapter.returnObj);
		param.add(thisParam);
		param.add(rtnParam);
		varType.put(thisParam.getName(), thisParam.getType());
		varType.put(rtnParam.getName(), rtnParam.getType());
		for (SingleVariableDeclaration para : parameters) {
			Parameter p = new Parameter(getMethodContext(), (Type) TypeAdapter.getInstance().transform(para.getType()),
					para.getName().toString());
			varType.put(p.getName(), p.getType());
			param.add(p);
		}
		return param;
	}

	@SuppressWarnings("unchecked")
	private void parseField() {
		for (FieldDeclaration field : fields) {
			org.eclipse.jdt.core.dom.Type jType = field.getType();
			Type sType = (Type) TypeAdapter.getInstance().transform(jType);
			List<VariableDeclarationFragment> list = field.fragments();
			for (VariableDeclarationFragment frag : list) {
				fieldType.put(frag.getName().getIdentifier(), sType);
			}
		}
	}

	public void insertVarDecl(String name, Type type) {
		varType.put(name, type);
	}

	public void insertUsedField(String name, Type type) {
		usedFieldType.put(name, type);
	}

	public Type getFieldTypeOf(String type, String field) {
		StructDefAdapter.insertField(type, field);
		String name = clazz.getName().toString();
		if (type.equals(name)) {
			return fieldType.get(field);
		} else {
			// TODO recursive check type
		}
		return null;
	}

	public Type getVarType(String var) {
		if (varType.containsKey(var))
			return varType.get(var);
		else if (fieldType.containsKey(var))
			return fieldType.get(var);
		else if (var.equals(AbstractASTAdapter.thisClass))
			return TypeAdapter.getType(clazz.getName().toString());
		else if (var.equals(AbstractASTAdapter.returnObj))
			return rtnType;
		return null;
	}

	public FENode getMethodContext() {
		return methodNode;
	}
}
