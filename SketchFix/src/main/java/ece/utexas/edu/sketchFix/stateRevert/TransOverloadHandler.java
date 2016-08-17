/**
 * @author Lisa Aug 16, 2016 TransOverloadHandler.java 
 */
package ece.utexas.edu.sketchFix.stateRevert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ece.utexas.edu.sketchFix.staticTransform.AbstractSketchTransformer;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.exprs.ExprConstChar;
import sketch.compiler.ast.core.exprs.ExprConstFloat;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprNamedParam;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;

public class TransOverloadHandler extends TransHandler {
	AbstractSketchTransformer ass;
	AbstractSketchTransformer source;
	Function suspFunc;

	public TransOverloadHandler(TransHandler handler, AbstractSketchTransformer assTransformer,
			AbstractSketchTransformer sourceTransformer) {
		ass = assTransformer;
		source = sourceTransformer;
		suspFunc = source.getCurrMethod();
		init();
	}

	protected void init() {
		List<Function> fromAss = ass.getMethods();
		String suspFun = suspFunc.getName();
		for (Function func : fromAss) {
			if (func.getName().equals(suspFun))
				return;
		}
		handleOverload();
	}

	private void handleOverload() {
		String suspFunc = source.getCurrMethod().getName();
		suspFunc = suspFunc.substring(0, suspFunc.indexOf("_"));
		for (Function func : ass.getMethods()) {
			String funName = func.getName();
			funName = funName.substring(0, funName.indexOf("_"));
			if (funName.equals(suspFunc)) {
				// OK add stmt in this method.
				insertOverload(func, source.getCurrMethod());
				break;
			}
		}
	}

	private void insertOverload(Function empty, Function susp) {
		List<Parameter> paraList = susp.getParams();
		List<Parameter> emPara = empty.getParams();
		// FIXME assume has fewer parameters, and parameters types diff
		HashMap<String, String> typeName = new HashMap<String, String>();
		HashMap<String, String> suspName = new HashMap<String, String>();
		for (Parameter para : emPara)
			typeName.put(para.getType().toString(), para.getName());
		for (Parameter para : paraList) {
			String tName = para.getType().toString();
			if (typeName.containsKey(tName))
				suspName.put(tName, typeName.get(tName));
			else
				suspName.put(tName, "");
		}
		List<Expression> params = new ArrayList<Expression>();
		for (Parameter para : paraList) {
			String name = suspName.get(para.getType().toString());
			if (!name.equals("")) {
				params.add(new ExprVar(para.getOrigin(), name));
			} else {
				Expression right = null;
				Type type = para.getType();
				if (type.isStruct())
					right = new ExprNew(para.getOrigin(), para.getType(), new ArrayList<ExprNamedParam>(), false);
				else if (type.equals(TypePrimitive.bittype) || type.equals(TypePrimitive.int32type))
					right = ExprConstInt.zero;
				else if (type.equals(TypePrimitive.floattype))
					right = ExprConstFloat.ZERO;
				else if (type.equals(TypePrimitive.chartype))
					right = ExprConstChar.zero;
				params.add(right);
			}
		}
		Expression funCall = new ExprFunCall(params.get(0).getOrigin(), susp.getName(), params);
		StmtExpr funDecl = new StmtExpr(params.get(0).getOrigin(), funCall);
		insertFunction(empty, funDecl);
	}

	private void insertFunction(Function empty, StmtExpr funDecl) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.params(empty.getParams());
		creator.name(empty.getName());
		List<Statement> body = new ArrayList<Statement>();
		body.add(funDecl);
		StmtBlock block = new StmtBlock(empty.getOrigin(), body);
		creator.body(block);
		Function function = creator.create();
		String name = empty.getName();
		for (int i = 0; i < methods.size(); i++) {
			if (methods.get(i).getName().equals(name)) {
				methods.remove(i);
				break;
			}
		}
		methods.add(function);

	}

}
