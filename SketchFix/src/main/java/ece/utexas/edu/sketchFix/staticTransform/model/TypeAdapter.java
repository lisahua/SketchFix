/**
 * @author Lisa Aug 1, 2016 TypeAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.cuda.typs.CudaMemoryType;

public class TypeAdapter extends AbstractASTAdapter {
	private static TypeAdapter instance = new TypeAdapter();
	private static HashMap<String, Type> typeMap = new HashMap<String, Type>();

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
		if (typeMap.containsKey(sType.toString()))
			return sType;
		// StructDefAdapter.insertStruct(sType.toString());
		typeMap.put(sType.toString(), sType);
		return sType;
	}

	public static Type getType(String name) {
		return typeMap.get(name);
	}
}
