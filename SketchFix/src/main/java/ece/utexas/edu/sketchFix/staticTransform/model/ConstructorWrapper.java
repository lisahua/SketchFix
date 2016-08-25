/**
 * @author Lisa Aug 3, 2016 MethodWrapper.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model;

import java.util.ArrayList;
import java.util.List;

public class ConstructorWrapper {

	String className;
	List<String> types = new ArrayList<String>();
	List<String> names = new ArrayList<String>();
	private int lastID = 0;

	public ConstructorWrapper(String type, List<String> paramTypes, int start) {
		className = type;
		types = paramTypes;
		for (int i = 0; i < paramTypes.size(); i++)
			names.add("_const" + (start + 1 + i));
		lastID = start + paramTypes.size();
	}

	public boolean matchConstructor(List<String> paramType) {
		if (paramType.size() != types.size())
			return false;
		for (int i = 0; i < paramType.size(); i++) {
			if (!matchType(paramType.get(i), types.get(i)))
				return false;
		}
		return true;
	}

	private boolean matchType(String declType, String realType) {
		if (declType.equals(realType))
			return true;
		else if (realType.equals("int"))
			return declType.equals("bit") || declType.equals("boolean");
		else if (realType.equals("char"))
			return declType.equals("String");
		else if (realType.equals("null"))
			return !declType.equals("bit") && !declType.equals("int") && declType.equals("double");
		return false;
	}

	public int getLastID() {
		return lastID;
	}

	public String getClassName() {
		return className;
	}

	public List<String> getTypes() {
		return types;
	}

	public List<String> getNames() {
		return names;
	}
	
	
}
