/**
 * @author Lisa Aug 19, 2016 NullExceptionHandler.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkCandidateGenerator;
import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
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

public class NullExceptionHandler extends FEReplacer {
	ExprFunCall lastCall = null;
	Parameter returnObj = null;
	private Function func = null;
	protected SkCandidateGenerator candGenerator = null;
	protected List<SkLinePy> scope;
	public NullExceptionHandler(AbstractRepairCandidate superClass) {
		scope = superClass.scope;
		candGenerator = superClass.candGenerator;
		if (scope == null)
			return;
		if (scope.get(0).getSkStmt() instanceof Function) {
			func = (Function) scope.get(0).getSkStmt();
			List<Parameter> params = func.getParams();
			Parameter param = params.get(params.size() - 1);
			if (param.getName().equals("returnObj"))
				returnObj = param;
		}
		for (SkLinePy line : scope) {
			if (line.getSkStmt() instanceof StmtExpr) {
				StmtExpr lastExpr = (StmtExpr) line.getSkStmt();
				if (lastExpr.getExpression() instanceof ExprFunCall) {
					lastCall = (ExprFunCall) lastExpr.getExpression();
				}
			}
		}

	}

	public Object visitStmtExpr(StmtExpr stmt) {
		Expression expr = stmt.getExpression();

		if (expr instanceof ExprFunCall) {
			if (expr.toString().equals(lastCall.toString())) {
				List<Statement> list = new ArrayList<Statement>();
				Expression invoker = ((ExprFunCall) expr).getParams().get(0);
				Expression exprBin = new ExprBinary(stmt.getOrigin(), ExprBinary.BINOP_EQ, invoker,
						ExprNullPtr.nullPtr);
				List<Statement> stmts = new ArrayList<Statement>();
				if (returnObj != null) {
					Expression rhs = candGenerator.getStmtAssign(func.getName(), returnObj.getType().toString());
					StmtAssign assign = new StmtAssign(stmt.getOrigin(),
							new ExprVar(stmt.getOrigin(), returnObj.getName()), rhs);
					stmts.add(assign);
				}
				stmts.add(new StmtReturn(stmt.getOrigin(), null));
				StmtIfThen ifThen = new StmtIfThen(stmt.getOrigin(), exprBin, new StmtBlock(stmt.getOrigin(), stmts),
						null);
				list.add(ifThen);
				list.add(stmt);
				return new StmtBlock(stmt.getOrigin(), list);
			}
		}
		return super.visitStmtExpr(stmt);
	}
}
