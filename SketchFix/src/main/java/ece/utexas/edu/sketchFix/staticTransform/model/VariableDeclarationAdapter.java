/**
 * @author Lisa Jul 31, 2016 VariableDeclarationAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import sketch.compiler.ast.core.exprs.*;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.typs.Type;

public class VariableDeclarationAdapter extends AbstractASTAdapter {
	private static VariableDeclarationAdapter instance = new VariableDeclarationAdapter();

	public static VariableDeclarationAdapter getInstance() {
		return instance;
	}

	@Override
	/**
	 * 
	 * @param node
	 *            VariableDeclarationStatement
	 * @return FE StmtVarDecl in Sketch Front End
	 */
	public Object transform(ASTNode node) {
		VariableDeclarationStatement stmt = (VariableDeclarationStatement) node;
		org.eclipse.jdt.core.dom.Type jType = stmt.getType();
		Type sType = (Type) TypeAdapter.getInstance().transform(jType);
		List<VariableDeclarationFragment> list = stmt.fragments();
		List<Type> types = new ArrayList<Type>();
		List<String> names = new ArrayList<String>();
		List<Expression> inits = new ArrayList<Expression>();
		types.add(sType);
		for (VariableDeclarationFragment frag : list) {
			names.add(frag.getName().getIdentifier());
			org.eclipse.jdt.core.dom.Expression init = frag.getInitializer();
			
		}
		StmtVarDecl sketchStmt = new StmtVarDecl(getContext(), types, names, inits);
		return sketchStmt;
	}

}
