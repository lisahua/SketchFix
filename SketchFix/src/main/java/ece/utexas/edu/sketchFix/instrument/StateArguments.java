/**
 * @author Lisa Jul 16, 2016 Arguments.java 
 */
package ece.utexas.edu.sketchFix.instrument;

public class StateArguments {
	private String srcDir = "";
	private String destination = "";
	private String testDir = "";
	private String traceFile = "";
private String instrumentDir = "";
	public StateArguments(String[] arg) {
		parseArguments(arg);
	}

	private void parseArguments(String[] args) {
		// Parse parameters
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--srcDir")) {
				srcDir = args[++i];
			} else if (args[i].equals("--testDir"))
				testDir = args[++i];
			else if (args[i].equals("--destination")) {
				destination = args[++i];
			}
			else if (args[i].equals("--traceFile")) {
				traceFile = args[++i];
			}	else if (args[i].equals("--instrumentDir")) {
				instrumentDir = args[++i];
			}
		}
	}

	public String getInstrumentDir() {
		return instrumentDir;
	}

	public void setInstrumentDir(String instrumentDir) {
		this.instrumentDir = instrumentDir;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTestDir() {
		return testDir;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public String getTraceFile() {
		return traceFile;
	}

	public void setTraceFile(String traceFile) {
		this.traceFile = traceFile;
	}

}
