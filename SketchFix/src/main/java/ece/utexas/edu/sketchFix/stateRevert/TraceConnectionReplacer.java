/**
 * @author Lisa Aug 16, 2016 InheritanceReplacer.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;

public class TraceConnectionReplacer extends FEReplacer {

	List<ASTLinePy> allLines = new ArrayList<ASTLinePy>();
	ExprFunCall atomCall = null;
	int lastCallID = 0;
	Program prog;
	String methodName;
	Function suspFunc;
	boolean directCalled = false;
	String testMethod;

	public TraceConnectionReplacer(List<ASTLinePy> allLines) {
		this.allLines = allLines;
		for (ASTLinePy line : allLines) {
			String name = line.getLinePyList().get(0).getMethodName();
			if (!name.contains("test"))
				methodName = name;
			else
				testMethod = name;
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
		// String stateBase = "_state";
		if (atomCall == null || directCalled)
			return super.visitFunction(func);
		if (func.getName().equals(atomCall.getName()))
			return handleConnectFunction(func);
		return super.visitFunction(func);
	}

	public Object visitExprFunCall(ExprFunCall call) {
		if (atomCall == null || directCalled)
			return super.visitExprFunCall(call);
		if (call.getName().equals(atomCall.getName())) {
			List<Expression> param = call.getParams();
			List<Expression> newParam = new ArrayList<Expression>();
			for (int i = 0; i < param.size() - 2; i++)
				newParam.add(param.get(i));
			if (param.get(param.size() - 2).toString().equals(AbstractASTAdapter.excepName)
					|| param.get(param.size() - 1).toString().equals(AbstractASTAdapter.excepName))
				return super.visitExprFunCall(call);
			else
				newParam.add(param.get(param.size() - 2));
			if (param.get(param.size() - 1).toString().equals(AbstractASTAdapter.returnObj)) {
				newParam.add(new ExprVar(call.getOrigin(), AbstractASTAdapter.excepName));
				newParam.add(param.get(param.size() - 1));
			} else {
				newParam.add(param.get(param.size() - 1));
				newParam.add(new ExprVar(call.getOrigin(), AbstractASTAdapter.excepName));
			}

			return new ExprFunCall(call.getOrigin(), call.getName(), newParam);
		}

		return super.visitExprFunCall(call);

	}

	private Object handleConnectFunction(Function func) {
		List<Statement> body = ((StmtBlock) func.getBody()).getStmts();
		if (body.size() > 0)
			return super.visitFunction(func);

		List<Expression> declParam = new ArrayList<Expression>();
		List<Statement> stmts = new ArrayList<Statement>();
		for (int i = 0; i < suspFunc.getParams().size() - 2; i++) {
			Parameter param = suspFunc.getParams().get(i);
			Type type = param.getType();
			StmtVarDecl stmt = new StmtVarDecl(func.getOrigin(), type, param.getName(), type.defaultValue());
			declParam.add(new ExprVar(func.getOrigin(), stmt.getName(0)));
			stmts.add(stmt);
		}
		Parameter param = suspFunc.getParams().get(suspFunc.getParams().size() - 2);
		if (!param.getName().equals(AbstractASTAdapter.excepName)) {
			Type type = param.getType();
			StmtVarDecl stmt = new StmtVarDecl(func.getOrigin(), type, param.getName(), type.defaultValue());
			stmts.add(stmt);
		}
		declParam.add(new ExprVar(func.getOrigin(), param.getName()));
		param = suspFunc.getParams().get(suspFunc.getParams().size() - 1);
		if (!param.getName().equals(AbstractASTAdapter.excepName)) {
			Type type = param.getType();
			StmtVarDecl stmt = new StmtVarDecl(func.getOrigin(), type, param.getName(), type.defaultValue());
			stmts.add(stmt);
		}
		declParam.add(new ExprVar(func.getOrigin(), param.getName()));

		ExprFunCall funCall = new ExprFunCall(func.getOrigin(), suspFunc.getName(), declParam);
		stmts.add(new StmtExpr(func.getOrigin(), funCall));
		ExprBinary bin = new ExprBinary(func.getOrigin(), ExprBinary.BINOP_EQ,
				new ExprVar(func.getOrigin(), AbstractASTAdapter.excepName), ExprConstInt.zero);
		StmtAssert assExcp = new StmtAssert(func.getOrigin(), bin, false, false);
		stmts.add(assExcp);
		StmtBlock block = new StmtBlock(func.getOrigin(), stmts);

		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.name(func.getName());

		creator.body(block);

		List<Parameter> funcParam = new ArrayList<Parameter>();
		for (int i = 0; i < func.getParams().size() - 1; i++)
			funcParam.add(func.getParams().get(i));
		if (func.getParams().get(func.getParams().size() - 1).getName().equals(AbstractASTAdapter.returnObj)) {
			funcParam.add(new Parameter(func.getOrigin(), TypePrimitive.bittype, AbstractASTAdapter.excepName));
			funcParam.add(func.getParams().get(func.getParams().size() - 1));
		} else {
			funcParam.add(func.getParams().get(func.getParams().size() - 1));
			funcParam.add(new Parameter(func.getOrigin(), TypePrimitive.bittype, AbstractASTAdapter.excepName));
		}
		creator.params(funcParam);
		return creator.create();
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
