/**
 * @author Lisa Aug 1, 2016 TypeAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import org.eclipse.jdt.core.dom.ASTNode;

import sketch.compiler.ast.cuda.typs.CudaMemoryType;

public class TypeAdapter extends AbstractASTAdapter {
	private static TypeAdapter instance = new TypeAdapter();

	public static TypeAdapter getInstance() {
		return instance;
	}

	@Override
	/**
	 * 
	 * @param node
	 *            Type in jdt
	 * @return Type in Sketch Front End
	 */
	public Object transform(ASTNode node) {
		org.eclipse.jdt.core.dom.Type jType = (org.eclipse.jdt.core.dom.Type) node;
		sketch.compiler.ast.core.typs.Type sType = new sketch.compiler.ast.core.typs.TypeStructRef(CudaMemoryType.LOCAL,
				jType.toString(), false);

		return sType;
	}

}
