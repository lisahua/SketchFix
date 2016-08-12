/**
 * @author Lisa Jul 30, 2016 Argument.java 
 */
package ece.utexas.edu.sketchFix.repair;

public class Argument {
	String traceFile = "";
	String[] sourceDir = null;
	String[] classDir = null;
	String skOrigin = "";

	public Argument(String[] arg) {
		for (int i = 0; i < arg.length; i++) {
			if (arg[i].equals("--traceFile"))
				traceFile = arg[++i];
			else if (arg[i].equals("--srcDir")) {
				String sDir = arg[++i].trim();
				sourceDir = sDir.split(",");
			} else if (arg[i].equals("--workDir")) {
				String cDir = arg[++i].trim();
				classDir = cDir.split(",");
			} else if (arg[i].equals("--skOrigin")) {
				skOrigin = arg[++i].trim();
			}
		}
	}

	public String getTraceFile() {
		return traceFile;
	}

	public void setTraceFile(String traceFile) {
		this.traceFile = traceFile;
	}

	public String[] getSourceDir() {
		return sourceDir;
	}

	public String[] getClassDir() {
		return classDir;
	}

	public String getSkOrigin() {
		return skOrigin;
	}

	public void setSkOrigin(String skOrigin) {
		this.skOrigin = skOrigin;
	}

	
	
}
