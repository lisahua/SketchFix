/**
 * @author Lisa Aug 30, 2016 TypeCandidateCollector.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.HashMap;
import java.util.HashSet;

import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeResolver;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeUsageRecorder;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.typs.Type;

public class TypeCandidateCollector {
	String method;
	HashMap<Type, HashSet<VarItem>> varTypeMap = new HashMap<Type, HashSet<VarItem>>();

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public HashMap<Type, HashSet<VarItem>> getVarTypeMap() {
		return varTypeMap;
	}

	public void insertVars(HashMap<String, Type> varType) {
		// TODO Auto-generated method stub

	}

	public void insertVarScope(HashMap<String, Statement> varScope) {
		// TODO Auto-generated method stub

	}

	public void insertExprType(HashMap<Expression, Type> exprType) {
		// TODO Auto-generated method stub

	}

	public void insertUsageRecorder(TypeUsageRecorder useRecorder) {
		// TODO Auto-generated method stub

	}

	public void insertTypeResolver(TypeResolver typeResolver) {
		// TODO Auto-generated method stub

	}

	public Expression getTypeCandInScope(FENode node) {
		return null;
	}

}
