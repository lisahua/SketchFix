/**
 * @author Lisa Jul 30, 2016 TestTextualLocalizer.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer;

import org.junit.Test;

import ece.utexas.edu.sketchFix.main.RepairMain;

public class TestRepairProcessor {
	@Test
	public void main() {
		// String[] arg = {
		// "org.jfree.chart.renderer.category.junit.AbstractCategoryItemRendererTests",
		// "test2947660" };
		String baseDir = "/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/";
		String[] arg = { "--sourceDir", baseDir + "source/," + baseDir + "tests/", "--classDir",
				baseDir + ".classes_instrumented/," + baseDir + ".test_instrumented/", "--traceFile",
				baseDir + ".trace_state.txt" };
		RepairMain.main(arg);
		// TextualFaultLocalizer tLocalizer = new TextualFaultLocalizer();

	}

}
