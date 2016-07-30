/**
 * @author Lisa Jul 27, 2016 Argument.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

public class Argument {
	private String srcDir = "";
	private String workDir = "";
	private String ignorePathFile = "";

	public Argument(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--srcDir"))
				srcDir = args[++i];
			else if (args[i].equals("--workDir"))
				workDir = args[++i];
			else if (args[i].equals("--ignorePathFile"))
				ignorePathFile = args[++i];

		}
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getWorkDir() {
		return workDir;
	}

	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	public String getIgnorePathFile() {
		return ignorePathFile;
	}

	public void setIgnorePathFile(String ignorePathFile) {
		this.ignorePathFile = ignorePathFile;
	}

}
