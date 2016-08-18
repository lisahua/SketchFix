/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.List;

import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.ExprFunCall;

public class OverloadReplacer extends FEReplacer {
	String fName;
	int paramChange;
	String target;

	public OverloadReplacer(Function func, String targetType, String sourceType) {
		target = targetType;
		fName = func.getName();
		List<Parameter> params = func.getParams();
		for (int i = 1; i < params.size() - 1; i++)
			if (params.get(i).getType().toString().equals(sourceType)) {
				paramChange = i;
				break;
			}
	}

	public Object visitExprFunCall(ExprFunCall stmt) {
		if (!stmt.getName().equals(fName))
			return super.visitExprFunCall(stmt);

		return super.visitExprFunCall(stmt);
	}

	public Object visitFunction(Function func) {
		if (!func.getName().equals(func))
			return super.visitFunction(func);

		return super.visitFunction(func);
	}

}
