/**
 * @author Lisa Jul 27, 2016 TestPreprocessing.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

public class TestPreprocessing {

	public static void main(String[] args) {
		String file = "/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/source/org/jfree/chart/plot/CompassPlot.java";
		String replace = file.replace("source", "work_dir");
		String[] dir = { "--srcDir", file, "--workDir", replace };
		new StaticParserProcessor(dir).process();
		
	}
}
