/**
 * @author Lisa Aug 5, 2016 VarDeclStmtAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.type.TypeAdapter;
import sketch.compiler.ast.core.Parameter;
import sketch.compiler.ast.core.exprs.ExprFunCall;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;

public class VarDeclStmtAdapter extends StatementAdapter {

	public VarDeclStmtAdapter(MethodDeclarationAdapter node) {
		super(node);
	}

	@Override
	public Object transform(ASTNode node) {
		VariableDeclarationStatement vds = (VariableDeclarationStatement) node;
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
			}
		}

		if (stmts.size() == 0)
			stmts.add(sketchStmt);
		return sketchStmt;
	}
}
