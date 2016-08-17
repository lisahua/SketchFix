/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;

import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.exprs.ExprNamedParam;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;

public class InheritanceReplacer extends FEReplacer {

	public Object visitStmtVarDecl(StmtVarDecl stmt) {
		Type type = stmt.getType(0);
		if (!type.isStruct())
			return super.visitStmtVarDecl(stmt);

		Expression expr = stmt.getInit(0);
		if (expr instanceof ExprNew) {
			ExprNew newIt = (ExprNew) expr;
			Type newType = newIt.getTypeToConstruct();
			if (!newType.toString().equals(type.toString())) {
				newIt = new ExprNew(stmt.getOrigin(), type, new ArrayList<ExprNamedParam>(), false);
			return  new StmtVarDecl(stmt.getOrigin(),type, stmt.getName(0),newIt);
				
			}
		}
		return super.visitStmtVarDecl(stmt);
	}

}
