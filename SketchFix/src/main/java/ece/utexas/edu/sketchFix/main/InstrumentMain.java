package ece.utexas.edu.sketchFix.main;

import ece.utexas.edu.sketchFix.instrument.InstrumentModel;

public class InstrumentMain {

	public static int instrument(String[] args) {
		try {
			new InstrumentModel(args).instrumentCode();
			
		} catch (Throwable throwable) {
			System.err.println(String.format("Failed while instrumenting code: %s", throwable.getMessage()));
			throwable.printStackTrace();
		}
		return 0;
	}

	public static void main(String[] args) {
		int returnValue = instrument(args);
		if (returnValue != 0) {
			System.exit(returnValue);
		}
	}
	
	
}
