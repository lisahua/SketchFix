/**
 * @author Lisa Jul 27, 2016 TestPreprocessing.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;


public class TestPreprocessing {
	
	public static void  main(String[] args) {
		String file = "/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/source";
		String replace = file.replace("source", "work_dir");
		String[] dir = { "--srcDir", file, "--workDir", replace, "--ignorePathFile", "preProcess.txt" };
		new StaticParserProcessor(dir).process();

	}
}
