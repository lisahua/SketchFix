/**
 * @author Lisa Jul 31, 2016 ASTAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.typs.Type;

public abstract class AbstractASTAdapter {
public final static String thisClass = "thisObj";
public final static String returnObj = "returnObj";
private static ExprVar thisObj ;
	/**
	 * Transform a ASTNode to Sketch Node
	 * @param node
	 * @return
	 */
	public abstract Object transform(ASTNode node);

	public static FENode getContext() {
		return null;
	}

	public static String getNextName() {
		return "";
	}
	
	public static ExprVar getThisObj() {
		if (thisObj==null) {
			thisObj = new ExprVar(getContext(),thisClass);
		}
		return thisObj;
	}
	
	public static void registerStruct(String name) {
		
	}
	
	public static void registerMethods(String name, Type invokerType, List<Type> param) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(name);
//		creator.params(param);
		// TODO add repair here
		Function function = creator.create();
	}
	
	
}
