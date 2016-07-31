/**
 * @author Lisa Jul 30, 2016 SuspiciousMethods.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer.model;

import java.util.TreeSet;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;

public class MethodData implements Comparable<MethodData> {

	String className = "";
	String classFullPath = "";
	String methodName = "";
	String key = "";
	int count;
	TreeSet<Integer> touchLines = new TreeSet<Integer>();
	boolean isTestMethod = false;

	public MethodData(String line) {
		String[] token = line.split("-");
		classFullPath = token[0];
		className = classFullPath.substring(classFullPath.lastIndexOf("/") + 1);
		methodName = token[1];
		key = line;
		// FIXME check test_dir is better
		isTestMethod = line.toLowerCase().contains("test");
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
	public int compareTo(MethodData data) {
		return count - data.count;
	}

	public MethodData setLinePy(LinePy oneLine) {
		touchLines.add(oneLine.getLineNum());
		insertCount();
		return this;
	}

	public String getClassFullPath() {
		return classFullPath;
	}

	public void setClassFullPath(String classFullPath) {
		this.classFullPath = classFullPath;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public TreeSet<Integer> getTouchLines() {
		return touchLines;
	}

	public void setTouchLines(TreeSet<Integer> touchLines) {
		this.touchLines = touchLines;
	}

	public boolean isTestMethod() {
		return isTestMethod;
	}

	public void setTestMethod(boolean isTestMethod) {
		this.isTestMethod = isTestMethod;
	}

	public double namingSimilarity(MethodData data) {
		String[] name1 = executeSingleName(key.replace("test", "").replace("Test", ""));
		String[] name2 = executeSingleName(data.key.replace("test", "").replace("Test", ""));
		int distance = minDistance(name1, name2);
		return distance / Math.max(name1.length, name2.length);
	}

	private boolean allUpperCase(String name) {
		char[] charArray = name.toCharArray();
		for (char c : charArray) {
			if (c >= 'a')
				return false;
		}
		return true;
	}

	private String[] executeSingleName(String name) {
		String[] tokens;
		if (allUpperCase(name)) {
			tokens = name.split("_");
		} else {
			tokens = name.split("(?=[A-Z][^A-Z])|_|-|/");
		}

		return tokens;
	}

	private int minDistance(String[] word1, String[] word2) {
		int len1 = word1.length;
		int len2 = word2.length;
		int[][] dp = new int[len1 + 1][len2 + 1];

		for (int i = 0; i <= len1; i++) {
			dp[i][0] = i;
		}

		for (int j = 0; j <= len2; j++) {
			dp[0][j] = j;
		}

		// iterate though, and check last char
		for (int i = 0; i < len1; i++) {
			String c1 = word1[i];
			for (int j = 0; j < len2; j++) {
				String c2 = word2[j];

				// if last two chars equal
				if (c1.equals(c2)) {
					// update dp value for +1 length
					dp[i + 1][j + 1] = dp[i][j];
				} else {
					int replace = dp[i][j] + 1;
					int insert = dp[i][j + 1] + 1;
					int delete = dp[i + 1][j] + 1;

					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					dp[i + 1][j + 1] = min;
				}
			}
		}

		return dp[len1][len2];
	}

	public String toString() {
		return key;
	}
}
