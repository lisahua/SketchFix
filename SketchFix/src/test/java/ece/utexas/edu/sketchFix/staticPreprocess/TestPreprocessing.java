/**
 * @author Lisa Jul 27, 2016 TestPreprocessing.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import org.junit.Test;

public class TestPreprocessing {
@Test
	public  void test() {
		String file = "/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/source/org/jfree/chart/JFreeChart.java";
		String replace = file.replace("source", "work_dir");
		String[] dir = { "--srcDir", file, "--workDir", replace };
		new StaticParserProcessor(dir).process();
		;
	}
}
