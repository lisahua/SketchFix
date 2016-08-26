/**
 * @author Lisa Jul 31, 2016 ASTAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StmtStateMapper;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeUsageRecorder;
import sketch.compiler.ast.core.FEContext;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.typs.Type;

public abstract class AbstractASTAdapter {
	public final static String thisClass = "thisObj";
	public final static String returnObj = "returnObj";
	protected static ExprVar thisObj = new ExprVar(getContext(), thisClass);
	private static ExprVar rtnObj = new ExprVar(getContext(), returnObj);;
	private static FENode packageNode = null;
	private static FENode methodNode = null;
	public static final String nextName = "_tmp";
	private static int nextNameCount = 0;
	public static final String pkgName = "sketchFix";
	protected static TypeUsageRecorder useRecorder = new TypeUsageRecorder();


	 public static final String excepName = "_exceptionObj";
	// public static final Type excepType = TypeAdapter.getType("Exception");
	/**
	 * Transform a ASTNode to Sketch Node
	 * 
	 * @param node
	 * @return
	 */
	public abstract Object transform(ASTNode node);

	public static TypeUsageRecorder getUseRecorder() {
		return useRecorder;
	}

	public static FENode getContext() {
		if (packageNode == null) {
			Program prog = Program.emptyProgram();
			packageNode = new sketch.compiler.ast.core.Package(prog, "", null, null, null, null);
		}
		return packageNode;
	}

	public static FENode getContextMethod() {
		if (methodNode == null) {

		}
		return methodNode;
	}

	public static String getNextName() {
		return nextName + (nextNameCount++);
	}

	public static ExprVar getThisObj() {
		return thisObj;
	}

	public static ExprVar getRtnObj() {
		return rtnObj;
	}

	public static void registerStruct(String name) {

	}

	private static void registerMethods(String name, Type invokerType, List<Type> param) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(name);
		// creator.params(param);
		// TODO add repair here
		Function function = creator.create();
	}

	public static FEContext getContext2() {
		return new FEContext();
	}

	public static Expression getDefaultValue(String type) {
		if (type.equals("int"))
			return new ExprConstInt(getContextMethod(), 0);
		else
			return new ExprNullPtr();
	}
}
