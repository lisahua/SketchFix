/**
 * @author Lisa Jul 26, 2016 TestStateParser.java 
 */
package ece.utexas.edu.sketchFix.instrument;

import org.junit.Test;

import ece.utexas.edu.sketchFix.instrument.restoreState.DynamicStateMapper;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.StaticSourceMapper;

public class TestStateParser {
	@Test
	public void testParseTraceFile() {
		String baseDir = "/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/";
		DynamicStateMapper dynamicParser = new DynamicStateMapper();
		String[] args = { baseDir + ".trace_state.txt", baseDir + ".tests_instrumented/",
				baseDir + ".classes_instrumented/" };
		dynamicParser.parseFiles(args);

		String file = "";
		// for (LinePy line : dynamicParser.getTrace()) {
		// String filePath = line.getFilePath();
		// LinePy staticInfo = dynamicParser.getFileLinePy(line.getFilePath(),
		// line.getLineNum());
		// if (file.equals(filePath)) {
		// if (staticInfo.getInstructions().size()==0)
		// System.out.println(staticInfo.getLineNum() + "\t" + "[Error]");
		// } else {
		// file = filePath;
		// System.out.println(filePath);
		// System.out.println(staticInfo.getLineNum() + "\t" +
		// staticInfo.getInstructions());
		// }
		// }

		StaticSourceMapper staticParser = new StaticSourceMapper(dynamicParser);
		String[] arg= {baseDir+"source/", baseDir+"tests/"};

		staticParser.parseFiles(arg);

		file = "";
		for (LinePy line : staticParser.getTrace()) {
			String filePath = line.getFilePath();
			LinePy staticInfo = staticParser.getFileLinePy(line.getFilePath(), line.getLineNum());
			if (file.equals(filePath)) {
				System.out.println(staticInfo.getLineNum() + "\t" + staticInfo.getSourceLine());
				if (staticInfo.getStoreStateString().length()>0) {
					System.out.println(staticInfo.getStoreStateString());
					System.out.println(staticInfo.getInstructions());
				}
				
			} else {
				file = filePath;
				System.out.println(filePath);
				System.out.println(staticInfo.getLineNum() + "\t" + staticInfo.getSourceLine());
				if (staticInfo.getStoreStateString().length()>0) {
					System.out.println(staticInfo.getStoreStateString());
					System.out.println(staticInfo.getInstructions());
				}
			}
		}
	}

}
