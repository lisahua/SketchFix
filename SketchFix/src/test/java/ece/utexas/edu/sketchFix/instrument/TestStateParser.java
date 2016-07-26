/**
 * @author Lisa Jul 26, 2016 TestStateParser.java 
 */
package ece.utexas.edu.sketchFix.instrument;

import org.junit.Test;

import ece.utexas.edu.sketchFix.instrument.restoreState.StateParser;

public class TestStateParser {
	@Test
	public void testParseTraceFile() {
		String baseDir = "/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/";
		StateParser parser = new StateParser();
		String[] args = { baseDir + ".trace_state.txt", baseDir + ".tests_instrumented/" };
		parser.parseFiles(args);
	}

}
