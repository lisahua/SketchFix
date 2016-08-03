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

import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprVar;
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

public class StatementAdapter extends AbstractASTAdapter {
	MethodDeclarationAdapter method;
	ExpressionAdapter exprAdapter;

	public StatementAdapter(MethodDeclarationAdapter node) {
		method = node;
		exprAdapter = new ExpressionAdapter(method);
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
			List<Statement> stmts = new ArrayList<Statement>();
			StmtVarDecl sketchStmt = new StmtVarDecl(method.getMethodContext(), types, names, inits);

			List<Expression> expList = new ArrayList<Expression>();
			for (int i = 0; i < inits.size(); i++) {
				Expression init = inits.get(i);
				if (init instanceof ExprFunCall) {
					ExprFunCall call = (ExprFunCall) init;
					List<Expression> expArg = call.getParams();
					expList.addAll(expArg);
					expList.add(new ExprVar(method.getMethodContext(), names.get(i)));
					ExprFunCall expCall = new ExprFunCall(method.getMethodContext(), call.getName(), expList);
					expArg = new ArrayList<Expression>();
					expArg.add(AbstractASTAdapter.getDefaultValue(sType.toString()));
					stmts.add(new StmtVarDecl(method.getMethodContext(), types, names, expArg));
					stmts.add(new StmtExpr(method.getMethodContext(), expCall));
					Parameter newParam = new Parameter(method.getMethodContext(), sType, names.get(i));
					StructDefGenerator.insertParamterToMethod(expCall.getName(), newParam);
				}
			}

			if (stmts.size() == 0)
				stmts.add(sketchStmt);
			return stmts;
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
		}
		// TODO more stmts such as for stmt

		return null;
	}

}
