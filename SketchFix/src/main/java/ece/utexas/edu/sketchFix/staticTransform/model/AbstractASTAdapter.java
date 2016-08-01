/**
 * @author Lisa Jul 31, 2016 ASTAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import org.eclipse.jdt.core.dom.ASTNode;

import sketch.compiler.ast.core.FENode;

public abstract class AbstractASTAdapter {

	/**
	 * Transform a ASTNode to Sketch Node
	 * @param node
	 * @return
	 */
	public abstract Object transform(ASTNode node);

	public static FENode getContext() {
		return null;
	}

	public static String getNextName() {
		return "";
	}
}
