/**
 * @author Lisa Aug 1, 2016 StatementAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
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
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.stmts.StmtWhile;
import sketch.compiler.ast.core.typs.Type;

public class StatementAdapter extends AbstractASTAdapter {
	MethodDeclarationAdapter method;
	ExpressionAdapter exprAdapter;
	List<Statement> stmtList = new ArrayList<Statement>();
	private VarDeclStmtAdapter varDeclAdapter;

	public StatementAdapter(MethodDeclarationAdapter node) {
		method = node;
		exprAdapter = new ExpressionAdapter(this);
		varDeclAdapter = new VarDeclStmtAdapter(node);
	}

	public void insertStmt(Statement stmt) {
		stmtList.add(stmt);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object transform(ASTNode node) {
		org.eclipse.jdt.core.dom.Statement stmt = (org.eclipse.jdt.core.dom.Statement) node;
		if (stmt instanceof VariableDeclarationStatement) {
			return varDeclAdapter.transform(stmt);
		} else if (stmt instanceof IfStatement) {
			IfStatement ifStmt = (IfStatement) stmt;
			org.eclipse.jdt.core.dom.Expression exp = ifStmt.getExpression();
			org.eclipse.jdt.core.dom.Statement thenStmt = ifStmt.getThenStatement();
			org.eclipse.jdt.core.dom.Statement elseStmt = ifStmt.getElseStatement();
			StmtIfThen skIfStmt = new StmtIfThen(method.getMethodContext(), (Expression) exprAdapter.transform(exp),
					(Statement) transform(thenStmt), (Statement) transform(elseStmt));
			return skIfStmt;
		} else if (stmt instanceof ReturnStatement) {
			ReturnStatement rtnStmt = (ReturnStatement) stmt;
			Expression right = (Expression) exprAdapter.transform(rtnStmt.getExpression());
			Expression left = AbstractASTAdapter.getRtnObj();
			StmtAssign assign = new StmtAssign(method.getMethodContext(), left, right);
			List<Statement> stmts = new ArrayList<Statement>();
			stmts.add(assign);
			stmts.add(new StmtReturn(method.getMethodContext(), null));
			return stmts;
		} else if (stmt instanceof WhileStatement) {
			WhileStatement whileStmt = (WhileStatement) stmt;
			StmtWhile skWhile = new StmtWhile(method.getMethodContext(),
					(Expression) exprAdapter.transform(whileStmt.getExpression()),
					(Statement) transform(whileStmt.getBody()));
			return skWhile;
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
			return new StmtAssert(method.getMethodContext(), sExpr, false);
		}
		// TODO more stmts such as for stmt

		return null;
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

}
