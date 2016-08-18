/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.List;

import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;

public class InvokerNotNullReplacer extends FEReplacer {

	public Object visitStmtVarDecl(Function func) {
		List<Statement> stmts = ((StmtBlock) func.getBody()).getStmts();
		if (stmts.size() == 0)
			return super.visitFunction(func);
		return super.visitFunction(func);
	}

}
