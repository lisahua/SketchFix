/**
 * @author Lisa Jul 16, 2016 CodeInstrumentationTask.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;

import ece.utexas.edu.sketchFix.main.StaticPreprocessMain;

public class PreprocessTask extends Task {
	String srcDir = "";
	String workDir = "";
	Path buildpath = null;
	String ignorePathFile="";
	
	public Path getBuildpath() {
		return buildpath;
	}

	public void setBuildpath(Path buildpath) {
		this.buildpath = buildpath;
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

	public String getIgnorePathFile() {
		return ignorePathFile;
	}

	public void setIgnorePathFile(String ignorePathFile) {
		this.ignorePathFile = ignorePathFile;
	}

	public void setWorkDir(String workDir) {
		if (workDir != null && !workDir.equals(""))
			this.workDir = workDir;
	}

	public void execute() {
		Java java = (Java) getProject().createTask("java");
		java.setTaskName(getTaskName());
		java.setDir(getProject().getBaseDir());
		java.setClassname(StaticPreprocessMain.class.getCanonicalName());
		java.createArg().setValue("--srcDir");
		java.createArg().setValue(srcDir);
		java.createArg().setValue("--workDir");
		java.createArg().setValue(workDir);
		java.createArg().setValue("--ignorePathFile");
		java.createArg().setValue(ignorePathFile);
		
		java.setClasspath(buildpath);

		if (java.executeJava() != 0) {
			
			throw new BuildException("Error copy classes. See messages above.");
		}
	}
}
