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
import sketch.compiler.ast.core.Function.FunctionCreator;
import sketch.compiler.ast.core.Parameter;
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
		methods = handler.methods;
		structs = handler.structs;
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
		List<Parameter> paraList = new ArrayList<Parameter>();
		paraList.addAll(susp.getParams());
		List<Parameter> emPara = new ArrayList<Parameter>();
		emPara.addAll(empty.getParams());
		// FIXME assume has fewer parameters, and parameters types diff
		HashMap<String, String> typeName = new HashMap<String, String>();
		HashMap<String, String> suspName = new HashMap<String, String>();
		// HashMap<String, String> suspName2 = new HashMap<String, String>();
		List<Expression> params = new ArrayList<Expression>();
		// if
		// (paraList.get(0).getType().toString().equals(emPara.get(0).getType().toString()))
		params.add(new ExprVar(paraList.get(0).getOrigin(), emPara.get(0).getName()));

		for (int i = 1; i < emPara.size() - 1; i++)
			typeName.put(emPara.get(i).getType().toString(), emPara.get(i).getName());
		for (int i = 1; i < paraList.size() - 1; i++) {
			String tName = paraList.get(i).getType().toString();
			if (typeName.containsKey(tName)) {
				suspName.put(tName, typeName.get(tName));
				typeName.remove(tName);
				continue;
			}
		}

		for (int i = 1; i < paraList.size() - 1; i++) {
			Parameter para = paraList.get(i);
			String pType = para.getType().toString();
			if (suspName.containsKey(pType)) {
				String name = suspName.get(pType);
				params.add(new ExprVar(paraList.get(i).getOrigin(), name));
				continue;
			}
			int max = 0;
			String maxType = "";
			for (String type : typeName.keySet()) {
				int sim = similarNames(type, pType);
				if (sim > max) {
					maxType = type;
					max = sim;
				}
			}
			if (!maxType.equals("")) {
				params.add(new ExprVar(para.getOrigin(), typeName.get(maxType)));
				typeName.remove(maxType);
				continue;
			}
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
		// add return
		params.add(new ExprVar(paraList.get(0).getOrigin(), emPara.get(emPara.size() - 1).getName()));

		Expression funCall = new ExprFunCall(params.get(0).getOrigin(), susp.getName(), params);
		StmtExpr funDecl = new StmtExpr(params.get(0).getOrigin(), funCall);

		insertFunction(empty, funDecl);
	}

	private void insertFunction(Function empty, StmtExpr funDecl) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		creator.params(empty.getParams());
		creator.name(empty.getName());
		List<Statement> body = new ArrayList<Statement>();
		String name = empty.getName();
		body.add(funDecl);
		StmtBlock block = new StmtBlock(empty.getOrigin(), body);
		creator.body(block);
		Function function = creator.create();
		
		for (int i = 0; i < methods.size(); i++) {
			String mName = methods.get(i).getName();
			if (mName.equals(name)) {
				methods.remove(i);
				break;
			}
		}
		
		
		methods.add(function);

	}

	private int similarNames(String testName, String sourceName) {
		String[] testTokens = executeSingleName(testName);
		String[] sourceTokens = executeSingleName(sourceName);
		int count = 0;
		for (String testT : testTokens) {
			for (String sourceT : sourceTokens) {
				if (sourceT.toLowerCase().equals(testT.toLowerCase()))
					count++;
			}
		}
		return count;
	}

	private boolean allUpperCase(String name) {
		char[] charArray = name.toCharArray();
		for (char c : charArray) {
			if (c >= 'a')
				return false;
		}
		return true;
	}

	private String[] executeSingleName(String name) {
		String[] tokens;
		if (allUpperCase(name)) {
			tokens = name.split("_");
		} else {
			tokens = name.split("(?=[A-Z][^A-Z])|_|-|/");
		}

		return tokens;
	}
}
