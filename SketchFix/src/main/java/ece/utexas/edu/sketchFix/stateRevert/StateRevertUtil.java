/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtIfThen;


public class StateRevertUtil extends FEReplacer {
	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();
	StmtIfThen atomIf = null;

	public StateRevertUtil(List<ASTLinePy> allLines) {
	}
}
