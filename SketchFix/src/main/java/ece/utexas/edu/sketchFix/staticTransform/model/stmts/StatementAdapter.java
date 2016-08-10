/**
 * @author Lisa Aug 1, 2016 StatementAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.expr.ExpressionAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import sketch.compiler.ast.core.FENode;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
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
			return new StmtBlock(method.getMethodContext(), skList);
		} else if (stmt instanceof ExpressionStatement) {
			ExpressionStatement exprStmt = (ExpressionStatement) stmt;
			org.eclipse.jdt.core.dom.Expression expr = exprStmt.getExpression();
			Expression sExpr = (Expression) exprAdapter.transform(expr);
			// if (sExpr != null)
			// return new StmtExpr(method.getMethodContext(), sExpr);
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

	public void insertUseMethod(String type, String name) {
		method.insertUseMethod(type, name);

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
//			Expression inExp = (Expression) exprAdapter.transform(init);
			// hacky for inherited methods
//			if (inExp instanceof ExprFunCall) {
//				ExprFunCall fCall = (ExprFunCall) inExp;
//				if (fCall.getParams().size() > 0) {
//					inExp = fCall.getParams().get(fCall.getParams().size() - 1);
//					Type last = exprAdapter.resolveType(inExp);
//					if (!last.toString().equals(sType.toString())) {
//						Expression rtn = new ExprVar(getMethodContext(), getNextName());
//						List<Expression> param = new ArrayList<Expression>();
//						param.addAll(fCall.getParams());
//						param.add(rtn);
//						Type invoker = exprAdapter.resolveType(fCall.getParams().get(0));
//						updateParaType(invoker.toString(), fCall.getName(), 10, sType.toString());
//						fCall = new ExprFunCall(getMethodContext(), fCall.getName(), param);
//					}
//				}
//				inits.add(fCall);
//			} else
				inits.add((Expression) exprAdapter.transform(init));
		}

		stmtList.add(new StmtVarDecl(method.getMethodContext(), types, names, inits));
	}

	@SuppressWarnings("unchecked")
	private StmtIfThen handleIfStmt(IfStatement ifStmt) {
		exprAdapter.setCurrVarType(TypePrimitive.bittype);
		org.eclipse.jdt.core.dom.Expression exp = ifStmt.getExpression();
		org.eclipse.jdt.core.dom.Statement thenStmt = ifStmt.getThenStatement();
		org.eclipse.jdt.core.dom.Statement elseStmt = ifStmt.getElseStatement();
		Object then = transform(thenStmt);
		Statement thenStatement = null;
		if (then != null && (then instanceof Statement)) {
			thenStatement = (Statement) then;
		} else if (then != null)
			thenStatement = new StmtBlock(method.getMethodContext(), (List<Statement>) then);

		Object elseSt = transform(elseStmt);
		Statement elseStatement = null;
		if (elseSt != null && (elseSt instanceof Statement)) {
			elseStatement = (Statement) elseSt;
		} else if (elseSt != null)
			elseStatement = new StmtBlock(method.getMethodContext(), (List<Statement>) elseSt);

		StmtIfThen skIfStmt = new StmtIfThen(method.getMethodContext(), (Expression) exprAdapter.transform(exp),
				thenStatement, elseStatement);
		return skIfStmt;
	}

	@SuppressWarnings("unchecked")
	private StmtWhile handleForStmt(ForStatement forStmt) {
		List<org.eclipse.jdt.core.dom.Expression> inits = forStmt.initializers();
		List<org.eclipse.jdt.core.dom.Expression> updater = forStmt.updaters();
		org.eclipse.jdt.core.dom.Expression cond = forStmt.getExpression();

		for (org.eclipse.jdt.core.dom.Expression exp : inits) {
			Object obj = exprAdapter.transform(exp);
			if (obj instanceof Statement) {
				stmtList.add((Statement) obj);
			}
		}
		List<Statement> updates = new ArrayList<Statement>();
		for (org.eclipse.jdt.core.dom.Expression exp : updater) {
			Object obj = exprAdapter.transform(exp);
			if (obj instanceof Statement) {
				updates.add((Statement) obj);
			}
		}
		StmtBlock body = (StmtBlock) transform(forStmt.getBody());
		updates.addAll(0, body.getStmts());

		StmtBlock block = new StmtBlock(method.getMethodContext(), updates);
		StmtWhile skWhile = new StmtWhile(method.getMethodContext(), (Expression) exprAdapter.transform(cond), block);
		return skWhile;
	}
}
