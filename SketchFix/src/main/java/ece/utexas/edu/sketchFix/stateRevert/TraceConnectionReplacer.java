/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;

public class TraceConnectionReplacer extends FEReplacer {

	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();
	ExprFunCall atomCall = null;
	int lastCallID = 0;
	Program prog;
	String methodName;
	Function suspFunc;
	boolean directCalled = false;

	public TraceConnectionReplacer(List<ASTLinePy> allLines) {
		this.allLines = allLines;
		for (ASTLinePy line : allLines) {
			String name = line.getLinePyList().get(0).getMethodName();
			if (!name.contains("test"))
				methodName = name;
		}

		for (int i = allLines.size() - 1; i >= 0; i--) {
			// FIXME I know its buggy
			for (Statement stmt : allLines.get(i).getSkStmts()) {
				if (stmt instanceof StmtExpr) {
					Expression expr = ((StmtExpr) stmt).getExpression();
					if (expr instanceof ExprFunCall) {
						atomCall = ((ExprFunCall) ((StmtExpr) stmt).getExpression());
						if (atomCall.equals(methodName))
							directCalled = true;
						lastCallID = i;
						// state = getState(i);
						return;
					}
				}
			}
		}

	}

	public Object visitProgram(Program prog) {
		this.prog = prog;
		List<Function> funcs = prog.getPackages().get(0).getFuncs();

		for (Function func : funcs) {
			if (func.getName().contains(methodName)) {
				suspFunc = func;
				break;
			}
		}
		return super.visitProgram(prog);
	}

	public Object visitFunction(Function func) {
		String stateBase = "_state";
		int i = 1;
		if (atomCall == null || directCalled)
			return super.visitFunction(func);
		if (!func.getName().equals(atomCall.getName()))
			return super.visitFunction(func);
			List<Expression> declParam = new ArrayList<Expression>();
			List<Statement> stmts = new ArrayList<Statement>();
			for (Parameter param : func.getParams()) {
				Type type = param.getType();
				StmtVarDecl stmt = new StmtVarDecl(func.getOrigin(), type, stateBase + (i++), type.defaultValue());
				declParam.add(new ExprVar(func.getOrigin(), stmt.getName(0)));
				// if (type instanceof TypePrimitive) {
				// TypePrimitive typeP = (TypePrimitive) type;
				// stmt = new StmtVarDecl(func.getOrigin(), type, stateBase +
				// (i++), typeP.defaultValue());
				// } else {
				// List<ExprNamedParam> skParam = new
				// ArrayList<ExprNamedParam>();
				// // Type newType = TypeAdapter.getType(type);
				// ExprNew newExpr = new ExprNew(func.getOrigin(), type,
				// skParam, false);
				// stmt = new StmtVarDecl(func.getOrigin(), type, stateBase +
				// (i++), newExpr);
				// }
				stmts.add(stmt);
			}
			ExprFunCall funCall = new ExprFunCall(func.getOrigin(), func.getName(), declParam);
			stmts.add(new StmtExpr(func.getOrigin(), funCall));
		

		return super.visitFunction(func);
	}

	private List<ASTLinePy> isTouched(StmtAssign stmt) {
		List<ASTLinePy> candidates = new ArrayList<ASTLinePy>();
		for (ASTLinePy line : allLines) {
			for (Statement st : line.getSkStmts()) {
				if (st instanceof StmtAssign) {
					if (((StmtAssign) st).getLHS().toString().equals(stmt.getLHS().toString()))
						candidates.add(line);
				}
			}
		}
		return candidates;
	}
}
