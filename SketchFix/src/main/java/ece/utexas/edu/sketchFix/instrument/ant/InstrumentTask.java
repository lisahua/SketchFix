/**
 * @author Lisa Jul 16, 2016 CodeInstrumentationTask.java 
 */
package ece.utexas.edu.sketchFix.instrument.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;

import ece.utexas.edu.sketchFix.main.InstrumentMain;

public class InstrumentTask extends Task {
	String srcDir = "";
	Path buildpath = null;
	String traceFile = ".trace.txt";
	String instrumentDir = "";
Path dependPath = null;


	public Path getDependPath() {
	return dependPath;
}

public void setDependPath(Path dependPath) {
	this.dependPath = dependPath;
}

	public String getInstrumentDir() {
		return instrumentDir;
	}

	public void setInstrumentDir(String instrumentDir) {
		this.instrumentDir = instrumentDir;
	}

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

	public String getTraceFile() {
		return traceFile;
	}

	public void setTraceFile(String traceFile) {
		if (traceFile != null && !traceFile.equals(""))
			this.traceFile = traceFile;
	}

	public void execute() {
		Java java = (Java) getProject().createTask("java");
		java.setTaskName(getTaskName());
		java.setDir(getProject().getBaseDir());
		java.setClassname(InstrumentMain.class.getCanonicalName());
		java.createArg().setValue("--srcDir");
		java.createArg().setValue(srcDir);
		java.setClasspath(buildpath);
		java.setClasspath(dependPath);
		java.createArg().setValue("--traceFile");
		java.createArg().setValue(traceFile);
		java.createArg().setValue("--instrumentDir");
		java.createArg().setValue(instrumentDir);

		
		if (java.executeJava() != 0) {
//			throw new BuildException("Error instrumenting classes. See messages above.");
		}
	}
}
