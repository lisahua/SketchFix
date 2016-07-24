/**
 * @author Lisa Jul 20, 2016 TestSliceLocalizer.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import ece.utexas.edu.sketchFix.slicing.LineData;
import ece.utexas.edu.sketchFix.slicing.SliceInputCollector;
import ece.utexas.edu.sketchFix.slicing.dynamicSlicing.SliceAnalyzer;

public class TestSliceLocalizer {

	public static void main(String[] arg) {
		SliceInputCollector collect = new SliceInputCollector();
		String[] negPath = { ".negTrace.txt" };
		String[] posPath = { ".posTrace1.txt", ".posTrace2.txt" };
		List<LineData> suspLocs = collect.compareTraces(negPath, posPath);
		FileInputStream is = null;
		

		try {
			is = new FileInputStream(
					"/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/.classes_instrumented/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.class");
			ClassReader cr = new ClassReader(is);
			ClassNode cn = new ClassNode();
			cr.accept(cn, ClassReader.SKIP_DEBUG);
			SliceAnalyzer analyzer = new SliceAnalyzer();
			analyzer.analyze(cn, suspLocs);

			// CheckClassAdapter.verify(cr, true, new PrintWriter(System.out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
