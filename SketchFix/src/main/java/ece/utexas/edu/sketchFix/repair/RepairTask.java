/**
 * @author Lisa Jul 16, 2016 CodeInstrumentationTask.java 
 */
package ece.utexas.edu.sketchFix.repair;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;

import ece.utexas.edu.sketchFix.main.RepairMain;

public class RepairTask extends Task {
	String srcDir = "";
	String workDir = "";

	Path buildpath = null;
	String traceFile = "";
	String skOrigin = "";

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

	public String getTraceFileFile() {
		return traceFile;
	}

	public void setTraceFile(String traceFile) {
		this.traceFile = traceFile;
	}

	public void setWorkDir(String workDir) {
		if (workDir != null && !workDir.equals(""))
			this.workDir = workDir;
	}

	public String getSkOrigin() {
		return skOrigin;
	}

	public void setSkOrigin(String skOrigin) {
		this.skOrigin = skOrigin;
	}

	public void execute() {
		Java java = (Java) getProject().createTask("java");
		java.setTaskName(getTaskName());
		java.setDir(getProject().getBaseDir());
		java.setClassname(RepairMain.class.getCanonicalName());
		java.createArg().setValue("--srcDir");
		java.createArg().setValue(srcDir);
		java.createArg().setValue("--workDir");
		java.createArg().setValue(workDir);
		java.createArg().setValue("--traceFile");
		java.createArg().setValue(traceFile);
		java.createArg().setValue("--skOrigin");
		java.createArg().setValue(skOrigin);

		java.setClasspath(buildpath);

		if (java.executeJava() != 0) {

			throw new BuildException("Error copy classes. See messages above.");
		}
	}
}
