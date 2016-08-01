/**
 * @author Lisa Aug 1, 2016 ExpressionAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;

import sketch.compiler.ast.core.exprs.ExprField;
import sketch.compiler.ast.core.exprs.ExprNamedParam;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.typs.Type;

public class ExpressionAdapter extends AbstractASTAdapter {

	@Override
	public Object transform(ASTNode node) {
		org.eclipse.jdt.core.dom.Expression expr = (org.eclipse.jdt.core.dom.Expression) node;
		sketch.compiler.ast.core.exprs.Expression skExpr = null;
		if (expr instanceof ClassInstanceCreation) {
			ClassInstanceCreation instNew = (ClassInstanceCreation) expr;
			org.eclipse.jdt.core.dom.Type type = instNew.getType();
			@SuppressWarnings("unchecked")
			List<org.eclipse.jdt.core.dom.Expression> param = instNew.arguments();
			List<ExprNamedParam> skParam = new ArrayList<ExprNamedParam>();
			for (org.eclipse.jdt.core.dom.Expression e : param) {
				skParam.add((ExprNamedParam) transform(e));
			}
			skExpr = new ExprNew(getContext(), (Type) TypeAdapter.getInstance().transform(type), skParam, false);
			return skExpr;
		} else if (expr instanceof FieldAccess) {
			FieldAccess jField = (FieldAccess) expr;
			org.eclipse.jdt.core.dom.Expression exp = jField.getExpression();
			skExpr = (sketch.compiler.ast.core.exprs.Expression) transform(exp);
			ExprField field = new ExprField(getContext(), skExpr, jField.getName().toString(), false);
			return field;
		} else if (expr instanceof ThisExpression) {
			ThisExpression thisExpr = (ThisExpression) expr;

		} else if (expr instanceof MethodInvocation) {
			MethodInvocation mtdInvoke = (MethodInvocation) expr;
			
		} else if (expr instanceof ConditionalExpression) {
			
		}
		return null;
	}

}
