/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.stmts.StmtIfThen;

public class ConditionTraceReplacer extends FEReplacer {
	
	public ConditionTraceReplacer(List<ASTLinePy> assLines,List<ASTLinePy> codeLines) {
		
	}

	public Object visitStmtIfThen(StmtIfThen ifStmt) {

		return super.visitStmtIfThen(ifStmt);
	}

	private void init(List<ASTLinePy> assLines,List<ASTLinePy> codeLines) {
		
	}
}
