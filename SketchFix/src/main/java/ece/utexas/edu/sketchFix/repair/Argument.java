/**
 * @author Lisa Jul 30, 2016 Argument.java 
 */
package ece.utexas.edu.sketchFix.repair;

public class Argument {
	String traceFile = "";
	String sourceDir = "";
	String classDir = "";

	public Argument(String[] arg) {
		for (int i = 0; i < arg.length; i++) {
			if (arg[i].equals("--traceFile"))
				traceFile = arg[++i];
			else if (arg[i].equals("--sourceDir"))
				sourceDir = arg[++i];
			else if (arg[i].equals("--classDir"))
				classDir = arg[++i];
		}
	}

	public String getTraceFile() {
		return traceFile;
	}

	public void setTraceFile(String traceFile) {
		this.traceFile = traceFile;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public String getClassDir() {
		return classDir;
	}

	public void setClassDir(String classDir) {
		this.classDir = classDir;
	}

}
