/**
 * @author Lisa Jul 30, 2016 TestTextualLocalizer.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer;

import ece.utexas.edu.sketchFix.main.RepairMain;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;

public class TestRepairProcessor {

	public static void main(String[] args) {
		// String[] arg = {
		// "org.jfree.chart.renderer.category.junit.AbstractCategoryItemRendererTests",
		// "test2947660" };
		String baseDir = "/Users/lisahua/Documents/lisa/project/build/Chart9_buggy/";
		LocalizerUtility.baseDir = baseDir + "work_dir/";
		LocalizerUtility.testDir = baseDir + "test_dir/";
		String[] arg = { "--sourceDir", baseDir + "work_dir/," + baseDir + "test_dir/", "--classDir",
				baseDir + ".classes_instrumented/," + baseDir + ".test_instrumented/", "--traceFile",
				baseDir + ".trace_state.txt" };
		RepairMain.main(arg);
		// TextualFaultLocalizer tLocalizer = new TextualFaultLocalizer();

	}

}
