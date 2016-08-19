/**
 * @author Lisa Aug 16, 2016 TransOverloadHandler.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import ece.utexas.edu.sketchFix.staticTransform.AbstractSketchTransformer;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import sketch.compiler.ast.core.FENode;
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
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;

public class OverloadHandler {
	List<Function> refFunctions = new ArrayList<Function>();
	HashSet<Integer> potential = new HashSet<Integer>();
	HashMap<String, String> replaceMap = new HashMap<String, String>();
	String funcName = "";
	List<String> destParam = new ArrayList<String>();
	List<String> replaceDestParam = new ArrayList<String>();
	HashMap<Integer, Function> combineFunc = new HashMap<Integer, Function>();
	List<StructDef> structs = new ArrayList<StructDef>();

	public OverloadHandler() {

	}

	public OverloadHandler(AbstractSketchTransformer refTransformer) {
		refFunctions = refTransformer.getMethods();
		structs = refTransformer.getStructs();
	}

	/**
	 * For each parameter from test, find the fittest para in destination
	 * methods.
	 * 
	 * @param index
	 */
	private void singleProcess(int index, String destName) {
		int[] mark = new int[destParam.size()];
		for (int i = 0; i < mark.length; i++)
			mark[i] = -1;
		Function func = refFunctions.get(index);
		List<String> testPara = new ArrayList<String>();
		for (int i = 1; i < func.getParams().size() - 1; i++)
			testPara.add(func.getParams().get(i).getType().toString());
		HashSet<String> destSet = new HashSet<String>(destParam);
		for (String pType : testPara) {
			if (destSet.contains(pType)) {
				mark[destParam.indexOf(pType)] = testPara.indexOf(pType)+1;
				destSet.remove(pType);
				continue;
			}
			int max = 0;
			String maxType = "";
			for (String type : destSet) {
				int sim = similarNames(type, pType);
				if (sim > max) {
					maxType = type;
					max = sim;
				}
			}
			if (!maxType.equals("")) {
				destSet.remove(maxType);
				mark[destParam.indexOf(maxType)] = testPara.indexOf(pType)+1;
				replaceMap.put(maxType, pType);
				continue;
			}
		}
		generateCombineFunc(mark, index, destName);
	}

	private void generateCombineFunc(int[] mark, int index, String destName) {
		FunctionCreator creator = new FunctionCreator(AbstractASTAdapter.getContext());
		Function func = refFunctions.get(index);
		creator.params(func.getParams());
		creator.name(func.getName());
		List<Statement> body = new ArrayList<Statement>();
		Statement stmt = generateStmt(mark, refFunctions.get(index), destName);
		body.add(stmt);
		StmtBlock block = new StmtBlock(func.getOrigin(), body);
		creator.body(block);
		Function function = creator.create();
		combineFunc.put(index, function);

	}

	private Statement generateStmt(int[] mark, Function testFunc, String destName) {
		// FIXME I assume the caller and return type is same for overload
		// methods
		List<Expression> exprParam = new ArrayList<Expression>();
		FENode node = testFunc.getOrigin();
		List<Parameter> testParam = testFunc.getParams();
		exprParam.add(new ExprVar(node, testParam.get(0).getName()));
		for (int i = 0; i < destParam.size(); i++) {
			if (mark[i] > -1) {
				exprParam.add(new ExprVar(node, testParam.get(i).getName()));
				replaceDestParam.add(testParam.get(mark[i]).getType().toString());
				continue;
			}
			Expression right = null;
			Type type = TypeAdapter.getType(destParam.get(i));
			replaceDestParam.add(destParam.get(i));
			if (type.isStruct())
				right = new ExprNew(node, type, new ArrayList<ExprNamedParam>(), false);
			else if (type.equals(TypePrimitive.bittype) || type.equals(TypePrimitive.int32type))
				right = ExprConstInt.zero;
			else if (type.equals(TypePrimitive.floattype))
				right = ExprConstFloat.ZERO;
			else if (type.equals(TypePrimitive.chartype))
				right = ExprConstChar.zero;
			exprParam.add(right);
		}
		exprParam.add(new ExprVar(node, testParam.get(testParam.size() - 1).getName()));
		Expression funCall = new ExprFunCall(node, destName, exprParam);
		return new StmtExpr(node, funCall);
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

	public boolean needOverload(MethodDeclaration currentMtd) {
		if (refFunctions.size() == 0)
			return false;
		String funcName = currentMtd.getName().toString();
		List<SingleVariableDeclaration> params = currentMtd.parameters();
		funcName = currentMtd.getName().toString();

		for (SingleVariableDeclaration val : params)
			destParam.add(val.getType().toString());

		for (int i = 0; i < refFunctions.size(); i++) {
			String name = refFunctions.get(i).getName();
			if (name.contains("_"))
				name = name.substring(0, name.indexOf("_"));
			if (!name.equals(funcName))
				continue;
			potential.add(i);
			List<Parameter> paraList = refFunctions.get(i).getParams();
			if (paraList.size() - 2 != destParam.size())
				continue;
			boolean res = true;
			for (int j = 1; j < paraList.size() - 1; i++) {
				String pType = paraList.get(j).getType().toString();
				if (!pType.equals(destParam.get(j - 1))) {
					res = false;
					break;
				}
			}
			if (res == false)
				continue;
			return false;
		}
		return true;
	}

	public String checkType(String type) {
		// if (replaceMap.size() == 0) {
		// // FIXME buggy when diff type paras
		// for (int i : potential)
		// singleProcess(i);
		// }
		return (replaceMap.containsKey(type) ? replaceMap.get(type) : null);
	}

	public void process(String destName, MethodDeclaration currentMtd) {
		needOverload(currentMtd);
		// FIXME buggy when diff type paras
		for (int i : potential)
			singleProcess(i, destName);
	}

	public String convertParam(int i) {
		return replaceDestParam.get(i);
	}

	public List<Function> getMethods(List<Function> sourceFun) {
		for (int i : combineFunc.keySet()) {
			refFunctions.remove(i);
			refFunctions.add(combineFunc.get(i));
		}
		List<String> refNames = new ArrayList<String>();
		for (Function func : refFunctions)
			refNames.add(func.getName());
		for (Function func : sourceFun) {
			if (refNames.contains(func.getName())) {
				int index = refNames.indexOf(func.getName());
				if (refFunctions.get(index).toString().length() < func.toString().length()) {
					refFunctions.remove(index);
				} else {
					continue;
				}
			}
			refFunctions.add(func);
		}

		return refFunctions;
	}

	public List<StructDef> getStructs(List<StructDef> source) {
		List<String> refNames = new ArrayList<String>();
		for (StructDef str : structs)
			refNames.add(str.getName());
		for (StructDef str : source) {
			if (refNames.contains(str.getName())) {
				int index = refNames.indexOf(str.getName());
				if (structs.get(index).getFields().size() < str.getFields().size()) {
					structs.remove(index);
				} else {
					continue;
				}
			}
			structs.add(str);
		}
		return structs;
	}
}
