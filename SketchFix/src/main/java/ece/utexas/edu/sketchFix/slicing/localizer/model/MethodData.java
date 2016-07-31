/**
 * @author Lisa Jul 30, 2016 SuspiciousMethods.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer.model;

public class MethodData implements Comparable {

	String className = "";
	String classFullPath = "";
	String methodName = "";
	String key = "";
	int count;

	public MethodData(String cName, String mName) {
		className = cName.substring(cName.lastIndexOf(".") + 1);
		classFullPath = cName.replace(".", "/");
		methodName = mName;
		key = classFullPath + "-" + methodName;
	}

	public MethodData(String line) {
		String[] token = line.split("-");
		classFullPath = token[0];
		className = classFullPath.substring(classFullPath.lastIndexOf("/") + 1);
		methodName = token[1];
		key = line;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void insertCount() {
		count++;
	}

	@Override
	public int compareTo(Object o) {
		MethodData data = (MethodData) o;
		return count - data.count;
	}

}
