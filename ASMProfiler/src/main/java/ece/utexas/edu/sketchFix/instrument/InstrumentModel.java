/**
 * @author Lisa Jul 16, 2016 InstrumentModel.java 
 */
package ece.utexas.edu.sketchFix.instrument;

public class InstrumentModel {
	private Arguments args;
//	private CodeInstrumentationTask instrumentationTask;

	public InstrumentModel(String[] arg) {
		args = new Arguments(arg);
	}

	public InstrumentModel instrumentCode() {
		
		return this;
	}

	private void parseArguments() {
		

	}
	
	
}
