/**
 * @author Lisa Aug 14, 2016 StateRequest.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import ece.utexas.edu.sketchFix.repair.processor.SkLineType;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.typs.Type;

public class VarItem {
	String varName;
	Type type;
	FENode scope;
	SkLineType scopeType;
	String funcName;

	public VarItem(String varName, Type type, FENode scope, SkLineType scopeType, String funcName) {
		this.varName = varName;
		this.type = type;
		this.scope = scope;
		this.scopeType = scopeType;
		this.funcName = funcName;
	}

	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public FENode getScope() {
		return scope;
	}

	public void setScope(FENode scope) {
		this.scope = scope;
	}


	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	
	
}