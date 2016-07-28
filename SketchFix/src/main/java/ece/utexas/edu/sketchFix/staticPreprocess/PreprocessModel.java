/**
 * @author Lisa Jul 27, 2016 PreprocessModel.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.util.Stack;

import org.eclipse.jdt.core.dom.TypeDeclaration;

public class PreprocessModel {

	public PreprocessModel(String name, StringBuilder naiveRewriter, StringBuilder stringBuilder) {
		className = name;
		getSets = naiveRewriter;
		node = stringBuilder;
	}

	StringBuilder getSets;
	StringBuilder node;
	String className;

	public boolean removeInner(PreprocessModel inner) {
		String head = "class " + inner.className+" ";

		int index = node.indexOf(head);
		if (index < 1)
			return false;

		String first = node.substring(0, index);
		String second = node.substring(index);
		int i = first.lastIndexOf("}");

		first = first.substring(0, i + 1);
		Stack<Character> paren = new Stack<Character>();
		for (i = second.indexOf("{"); i < second.length(); i++) {
			if (second.charAt(i) == '{')
				paren.push('{');
			else if (second.charAt(i) == '}')
				paren.pop();
			else if (paren.isEmpty())
				break;
		}
		second = second.substring(i);
		node = new StringBuilder(first);

		String innerNode = inner.node.substring(0, inner.node.lastIndexOf("}"));

		node.append(innerNode).append(inner.getSets).append("}");
		node.append(second.substring(0, second.lastIndexOf("}"))).append(getSets).append("}");
		return true;
	}

}
