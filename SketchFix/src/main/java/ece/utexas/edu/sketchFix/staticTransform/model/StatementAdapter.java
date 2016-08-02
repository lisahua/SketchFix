/**
 * @author Lisa Aug 1, 2016 StatementAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.stmts.StmtWhile;
import sketch.compiler.ast.core.typs.Type;

public class StatementAdapter extends AbstractASTAdapter {
	MethodDeclarationAdapter method;
	ExpressionAdapter exprAdapter = new ExpressionAdapter(method);

	public StatementAdapter(MethodDeclarationAdapter node) {
		method = node;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object transform(ASTNode node) {
		org.eclipse.jdt.core.dom.Statement stmt = (org.eclipse.jdt.core.dom.Statement) node;
		if (stmt instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement vds = (VariableDeclarationStatement) stmt;
			org.eclipse.jdt.core.dom.Type jType = vds.getType();
			Type sType = (Type) TypeAdapter.getInstance().transform(jType);
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
			StmtVarDecl sketchStmt = new StmtVarDecl(getContext(), types, names, inits);
			return sketchStmt;
		} else if (stmt instanceof IfStatement) {
			IfStatement ifStmt = (IfStatement) stmt;
			org.eclipse.jdt.core.dom.Expression exp = ifStmt.getExpression();
			org.eclipse.jdt.core.dom.Statement thenStmt = ifStmt.getThenStatement();
			org.eclipse.jdt.core.dom.Statement elseStmt = ifStmt.getElseStatement();
			StmtIfThen skIfStmt = new StmtIfThen(getContext(),
					(Expression) exprAdapter.transform(exp), (Statement) transform(thenStmt),
					(Statement) transform(elseStmt));
			return skIfStmt;
		} else if (stmt instanceof ReturnStatement) {
			ReturnStatement rtnStmt = (ReturnStatement) stmt;
			StmtReturn skRtnStmt = new StmtReturn(getContext(),
					(Expression) exprAdapter.transform(rtnStmt.getExpression()));
			return skRtnStmt;
		} else if (stmt instanceof WhileStatement) {
			WhileStatement whileStmt = (WhileStatement) stmt;
			StmtWhile skWhile = new StmtWhile(getContext(),
					(Expression) exprAdapter.transform(whileStmt.getExpression()),
					(Statement) transform(whileStmt.getBody()));
			return skWhile;
		} else if (stmt instanceof Block) {
			List<org.eclipse.jdt.core.dom.Statement> list = ((Block) stmt).statements();
			List<Statement> skList = new ArrayList<Statement>();
			for (org.eclipse.jdt.core.dom.Statement one : list) {
				skList.add((Statement) transform(one));
			}
			return new StmtBlock(getContext(), skList);
		}
		// TODO more stmts

		return null;
	}

}
