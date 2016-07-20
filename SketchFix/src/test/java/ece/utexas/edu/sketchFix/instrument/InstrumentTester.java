package ece.utexas.edu.sketchFix.instrument;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

public class InstrumentTester {
	public static void main(String[] args) {
		FileInputStream is=null;
		try {
			is = new FileInputStream(
					"/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/.classes_instrumented/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.class");
			ClassReader cr = new ClassReader(is);
//			 ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//			cr.accept(new ASMifierClassVisitor(), 0);
			
//			FileOutputStream fos = new FileOutputStream("Here.class");
//			fos.write(cw.toByteArray());
//			fos.close();
//			is = new FileInputStream("Here.class");
//			cr = new ClassReader(is);
			CheckClassAdapter.verify(cr, true, new PrintWriter("tmp.txt"));
//			cr.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter("tmp.txt")), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		

	}
}
