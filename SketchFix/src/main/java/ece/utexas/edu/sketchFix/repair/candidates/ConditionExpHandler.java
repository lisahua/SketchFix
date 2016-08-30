/**
 * @author Lisa Aug 19, 2016 NullExceptionHandler.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.CandidateTemplate;
import ece.utexas.edu.sketchFix.repair.processor.SkCandidate;
import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.repair.processor.SkLineType;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;

public class ConditionExpHandler extends CandidateTemplate  {
	ExprFunCall lastCall = null;
	Parameter returnObj = null;
	private Function func = null;
	private int lastCallID = 0;

	public ConditionExpHandler(SkCandidate generator) {
		super(generator);
		if (scope.get(0).getSkStmt() instanceof Function) {
			func = (Function) scope.get(0).getSkStmt();
			List<Parameter> params = func.getParams();
			if (params.size() > 0) {
				Parameter param = params.get(params.size() - 1);
				if (param.getName().equals("returnObj"))
					returnObj = param;
			}
		}
		for (int i = 0; i < scope.size(); i++) {
			if (scope.get(i).getSkStmt() instanceof StmtExpr) {
				StmtExpr lastExpr = (StmtExpr) scope.get(i).getSkStmt();
				if (lastExpr.getExpression() instanceof ExprFunCall) {
					lastCall = (ExprFunCall) lastExpr.getExpression();
					lastCallID = i;
				}
			}
		}

	}

	

	public List<SkLinePy> getScope() {
		return scope;
	}



	@Override
	protected boolean hasDone() {
		// TODO Auto-generated method stub
		return false;
	}

}
