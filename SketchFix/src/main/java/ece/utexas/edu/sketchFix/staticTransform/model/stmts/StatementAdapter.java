/**
 * @author Lisa Aug 1, 2016 StatementAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.expr.ExpressionAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.stmts.StmtWhile;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;

public class StatementAdapter extends AbstractASTAdapter {
	MethodDeclarationAdapter method;
	ExpressionAdapter exprAdapter;
	List<Statement> stmtList = new ArrayList<Statement>();

	public StatementAdapter(MethodDeclarationAdapter node) {
		method = node;
		exprAdapter = new ExpressionAdapter(this);
		// mapper =new StmtStateMapper(node.getLinePyGenerator().getTrace(),
		// list,baseDir);
	}

	public void insertStmt(Statement stmt) {
		stmtList.add(stmt);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public Object transform(ASTNode node) {
		org.eclipse.jdt.core.dom.Statement stmt = (org.eclipse.jdt.core.dom.Statement) node;
		stmtList.clear();
		if (stmt instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement vds = (VariableDeclarationStatement) node;
			handleVarDecl(vds);
			return stmtList;
		} else if (stmt instanceof IfStatement) {
			IfStatement ifStmt = (IfStatement) stmt;
			return handleIfStmt(ifStmt);
		} else if (stmt instanceof ReturnStatement) {
			ReturnStatement rtnStmt = (ReturnStatement) stmt;
			Expression right = (Expression) exprAdapter.transform(rtnStmt.getExpression());
			Expression left = AbstractASTAdapter.getRtnObj();

			StmtAssign assign = new StmtAssign(method.getMethodContext(), left, right);
			stmtList.add(assign);

			stmtList.add(new StmtReturn(method.getMethodContext(), null));
			insertState(stmt, exprAdapter.resolveType(left).toString(), stmtList);
			return stmtList;
		} else if (stmt instanceof WhileStatement) {
			WhileStatement whileStmt = (WhileStatement) stmt;
			StmtWhile skWhile = new StmtWhile(method.getMethodContext(),
					(Expression) exprAdapter.transform(whileStmt.getExpression()),
					(Statement) transform(whileStmt.getBody()));
			return skWhile;
		} else if (stmt instanceof ForStatement) {
			ForStatement forStmt = (ForStatement) stmt;
			return handleForStmt(forStmt);
		} else if (stmt instanceof EnhancedForStatement) {
			EnhancedForStatement forStmt = (EnhancedForStatement) stmt;
			// TODO I dont know now
		} else if (stmt instanceof Block) {
			List<org.eclipse.jdt.core.dom.Statement> list = ((Block) stmt).statements();
			List<Statement> skList = new ArrayList<Statement>();
			for (org.eclipse.jdt.core.dom.Statement one : list) {
				Object obj = transform(one);
				if (obj instanceof Statement)
					skList.add((Statement) transform(one));
				else
					skList.addAll((List<Statement>) transform(one));
			}
			insertState(stmt, "", skList);
			return new StmtBlock(method.getMethodContext(), skList);
		} else if (stmt instanceof ExpressionStatement) {

			ExpressionStatement exprStmt = (ExpressionStatement) stmt;
			org.eclipse.jdt.core.dom.Expression expr = exprStmt.getExpression();
			Object obj = exprAdapter.transform(expr);
			if (obj instanceof Statement) {
				stmtList.add((Statement) obj);
				// FIXME buggy
			}
				insertState(exprStmt, "", stmtList);
			// if (sExpr != null)
			// return new StmtExpr(method.getMethodContext(), sExpr);
		} else if (stmt instanceof ThrowStatement) {
			// mapper.insertStmt(stmt, AbstractASTAdapter.excepType.toString());
			ThrowStatement throwStmt = (ThrowStatement) stmt;
			Object obj = exprAdapter.transform(throwStmt.getExpression());

			// StmtVarDecl decl = new StmtVarDecl(method.getMethodContext(),
			// TypeAdapter.getType(name),
			// AbstractASTAdapter.excepName, (Expression) obj);
			// stmtList.add(decl);
			
			stmtList.add(new StmtReturn(method.getMethodContext(), null));
			insertState(throwStmt,"Exception", stmtList);
			return stmtList;
		}
		// TODO more stmts such as for stmt

		return stmtList;
	}

	public FENode getMethodContext() {
		return method.getMethodContext();
	}

	public MethodWrapper getMethodModel(String invokerType, String string) {
		return method.getMethodModel(invokerType, string);
	}

	public Type getMethodReturnType(String type, String name) {
		return method.getMethodReturnType(type, name);
	}

	public Type getFieldTypeOf(String type, String field) {
		return method.getFieldTypeOf(type, field);
	}

	public Type getVarType(String name) {
		return method.getVarType(name);
	}

	public void insertVarDecl(String name, Type type) {
		method.insertVarDecl(name, type);
	}

	public String getLastInsertVarName(String type) {
		for (int i = stmtList.size() - 1; i > -1; i--) {
			if (stmtList.get(i) instanceof StmtVarDecl) {
				StmtVarDecl varDecl = (StmtVarDecl) stmtList.get(i);
				if (varDecl.getType(0).toString().equals(type))
					return varDecl.getName(0);
			}
		}
		return "";
	}

	public String getLastInsertVarName() {
		for (int i = stmtList.size() - 1; i > -1; i--) {
			if (stmtList.get(i) instanceof StmtVarDecl) {
				StmtVarDecl varDecl = (StmtVarDecl) stmtList.get(i);
				return varDecl.getName(0);
			}
		}
		return "";
	}

	public void updateParaType(String classType, String methodName, int id, String type) {
		method.updateParaType(classType, methodName, id, type);
	}

	@SuppressWarnings("unchecked")
	private void handleVarDecl(VariableDeclarationStatement vds) {
		org.eclipse.jdt.core.dom.Type jType = vds.getType();
		Type sType = TypeAdapter.getType(jType.toString());

		exprAdapter.setCurrVarType(sType);
		List<VariableDeclarationFragment> list = vds.fragments();
		List<Type> types = new ArrayList<Type>();
		List<String> names = new ArrayList<String>();
		List<Expression> inits = new ArrayList<Expression>();
		types.add(sType);
		for (VariableDeclarationFragment frag : list) {
			String name = frag.getName().getIdentifier();
			method.insertVarDecl(name, sType);
			names.add(name);
			org.eclipse.jdt.core.dom.Expression init = frag.getInitializer();

			inits.add((Expression) exprAdapter.transform(init));
		}
		StmtVarDecl varDecl = new StmtVarDecl(method.getMethodContext(), types, names, inits);
		stmtList.add(varDecl);
//		for (Statement stmt: stmtList)
		insertState(vds, sType.toString(), stmtList);
	}

	@SuppressWarnings("unchecked")
	private Object handleIfStmt(IfStatement ifStmt) {

		org.eclipse.jdt.core.dom.Expression exp = ifStmt.getExpression();
		org.eclipse.jdt.core.dom.Statement thenStmt = ifStmt.getThenStatement();
		org.eclipse.jdt.core.dom.Statement elseStmt = ifStmt.getElseStatement();
		exprAdapter.clearCurrVarType();
		Object then = transform(thenStmt);
		Statement thenStatement = null;
		if (then != null && (then instanceof Statement)) {
			thenStatement = (Statement) then;
		} else if (then != null)
			thenStatement = new StmtBlock(method.getMethodContext(), (List<Statement>) then);
		stmtList.clear();
		Statement elseStatement = null;
		if (elseStmt != null) {
			Object elseSt = transform(elseStmt);
			if (elseSt != null && (elseSt instanceof Statement)) {
				elseStatement = (Statement) elseSt;
			} else if (elseSt != null)
				elseStatement = new StmtBlock(method.getMethodContext(), (List<Statement>) elseSt);
		}
		stmtList.clear();
		exprAdapter.setCurrVarType(TypePrimitive.bittype);
		Expression skExp = (Expression) exprAdapter.transform(exp);
		StmtIfThen skIfStmt = new StmtIfThen(method.getMethodContext(), skExp, thenStatement, elseStatement);
	
		stmtList.add(skIfStmt);
		insertState(ifStmt, "bit", stmtList);
		return stmtList;
	}

	@SuppressWarnings("unchecked")
	private Object handleForStmt(ForStatement forStmt) {
		List<org.eclipse.jdt.core.dom.Expression> inits = forStmt.initializers();
		List<org.eclipse.jdt.core.dom.Expression> updater = forStmt.updaters();
		org.eclipse.jdt.core.dom.Expression cond = forStmt.getExpression();
		List<Statement> initList = new ArrayList<Statement>();
		for (org.eclipse.jdt.core.dom.Expression exp : inits) {
			Object obj = exprAdapter.transform(exp);
			if (obj instanceof Statement) {
				initList.add((Statement) obj);
			} else if (obj instanceof Expression)
				initList.add(new StmtExpr(getMethodContext(), (Expression) obj));
		}
		List<Statement> updates = new ArrayList<Statement>();
		stmtList.clear();
		StmtBlock body = (StmtBlock) transform(forStmt.getBody());
		updates.addAll(body.getStmts());
		stmtList.clear();
		for (org.eclipse.jdt.core.dom.Expression exp : updater) {
			Object obj = exprAdapter.transform(exp);
			if (obj instanceof Statement) {
				updates.add((Statement) obj);
			} else if (obj instanceof Expression) {
				updates.addAll(stmtList);
				stmtList.clear();
			}
		}
		StmtBlock block = new StmtBlock(method.getMethodContext(), updates);
		StmtWhile skWhile = new StmtWhile(method.getMethodContext(), (Expression) exprAdapter.transform(cond), block);
		initList.add(skWhile);
		insertState(forStmt, "bit", initList);
		return initList;
	}

	// public ExprNew getNewException() {
	// return method.getNewException();
	// }
	// private void insertUsedField(String type, String field) {
	//
	// }

	public String getVarOfType(Type type) {
		return method.getVarOfType(type);
	}

	public void insertState(org.eclipse.jdt.core.dom.Statement stmt, String type, Object skStmt) {
		method.getStateMapper().insertStmt(stmt, type, skStmt);
	}

	public void setOverloadMap(HashMap<String, String> overload) {
		// TODO Auto-generated method stub

	}
}
