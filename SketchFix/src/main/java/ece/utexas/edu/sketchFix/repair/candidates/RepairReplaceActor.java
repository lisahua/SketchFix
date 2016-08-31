/**
 * @author Lisa Aug 30, 2016 RepairReplaceActor.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.postProcessor.RepairOpType;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;

public class RepairReplaceActor extends FEReplacer {
	RepairItem repairItem;

	public RepairReplaceActor(RepairItem item) {
		repairItem = item;
	}

	public Object visitFunction(Function func) {
		if (!func.equals(repairItem.getScope()))
			return super.visitFunction(func);
		List<Statement> newStmts = new ArrayList<Statement>();
		List<Statement> stmts = ((StmtBlock) func.getBody()).getStmts();
		for (Statement stmt : stmts) {
			if (!stmt.equals(repairItem.getInsertPoint()))
				newStmts.add(stmt);
			else {
				if (repairItem.getRepairType() == RepairOpType.ADDBEFORE) {
					newStmts.add(repairItem.getReplaceStmt());
					newStmts.add(stmt);
				} else if (repairItem.getRepairType() == RepairOpType.ADDAFTER) {
					newStmts.add(stmt);
					newStmts.add(repairItem.getReplaceStmt());
				}
			}
		}
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(func.getName());
		creator.params(func.getParams());
		List<Statement> body = new ArrayList<Statement>();
		creator.body(new StmtBlock(func.getOrigin(), body));
		return creator.create();
	}
	
}
