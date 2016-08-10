/**
 * @author Lisa Aug 1, 2016 TypeAdapter.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.type;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StructDefGenerator;
import sketch.compiler.ast.core.typs.Type;
import sketch.compiler.ast.core.typs.TypePrimitive;
import sketch.compiler.ast.cuda.typs.CudaMemoryType;

public class TypeAdapter {
	private static TypeAdapter instance = new TypeAdapter();
	private static HashMap<String, Type> typeMap = new HashMap<String, Type>();

	public static TypeAdapter getInstance() {
		return instance;
	}

	// @Override
	/**
	 * 
	 * @param node
	 *            Type in jdt
	 * @return Type in Sketch Front End
	 */
	// public Object transform(ASTNode node) {
	// String name = node.toString();
	// if (node instanceof org.eclipse.jdt.core.dom.Type) {
	// // org.eclipse.jdt.core.dom.Type jType =
	// // (org.eclipse.jdt.core.dom.Type) node;
	// name = node.toString();
	// } else if (node instanceof TypeDeclaration) {
	// name = ((TypeDeclaration) node).getName().toString();
	// }
	// sketch.compiler.ast.core.typs.Type sType = new
	// sketch.compiler.ast.core.typs.TypeStructRef(
	// CudaMemoryType.UNDEFINED, name, false);
	// if (typeMap.containsKey(sType.toString()))
	// return sType;
	//// StructDefGenerator.insertStruct(sType.toString());
	// typeMap.put(sType.toString(), sType);
	// return sType;
	// }

	// private Type recordField(org.eclipse.jdt.core.dom.Type node) {
	// String name = node.toString();
	// name = node.toString();
	// Type sType = new
	// sketch.compiler.ast.core.typs.TypeStructRef(CudaMemoryType.UNDEFINED,
	// name, false);
	// if (typeMap.containsKey(sType.toString()))
	// return sType;
	// typeMap.put(sType.toString(), sType);
	// return sType;
	// }

	public static Type getType(String name) {
		if (name == null || name.equals("void"))
			return null;
		Type type;
		if (name.equals("int"))
			type = (typeMap.containsKey(name)) ? typeMap.get(name) : TypePrimitive.inttype;
		else if (name.equals("bit"))
			type = (typeMap.containsKey(name)) ? typeMap.get(name) : TypePrimitive.bittype;
		else if (name.equals("float"))
			type = (typeMap.containsKey(name)) ? typeMap.get(name) : TypePrimitive.doubletype;
			else if (name.equals("bit")|| name.equals("boolean"))
				type = (typeMap.containsKey(name)) ? typeMap.get(name) : TypePrimitive.bittype;
		else
			type = (typeMap.containsKey(name)) ? typeMap.get(name)
					: new sketch.compiler.ast.core.typs.TypeStructRef(CudaMemoryType.UNDEFINED, name, false);
		typeMap.put(name, type);
		return type;
	}

	public static HashMap<String, Type> getTypeMap() {
		return typeMap;
	}

	public static void insertArrayType(String name,Type arrType) {
		typeMap.put(name, arrType);
	}
}
