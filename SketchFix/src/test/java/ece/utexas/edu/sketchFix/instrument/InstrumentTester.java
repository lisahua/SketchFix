package ece.utexas.edu.sketchFix.instrument;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.CheckClassAdapter;

import ece.utexas.edu.sketchFix.slicing.stateRecord.StateClassVisitor;

public class InstrumentTester {

	public static void main(String[] args) {
		FileInputStream is = null;
		try {
			is = new FileInputStream(
					"/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/build/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.class");
			ClassReader cr = new ClassReader(is);
			// FileInputStream is = new FileInputStream(file);
			// ClassReader cr = new ClassReader(is);
			ClassNode cn = new ClassNode();
			ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
			cr.accept(cn, 0);
			 cn.accept(new StateClassVisitor(cw));
			// cr.accept(cn, 0);
			
//			cn.accept(cw);

			// cr.accept(new LineNumberClassVisitor(cw), 0);

			// FileOutputStream fos = new
			// FileOutputStream(getInstrumentDir(file));
			// fos.write(cw.toByteArray());
			// fos.close();
			// ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			// cr.accept(new ASMifierClassVisitor(), 0);

//			FileOutputStream fos = new FileOutputStream("Here.class");
//			fos.write(cw.toByteArray());
//			fos.close();
//			is = new FileInputStream("Here.class");
//			cr = new ClassReader(is);
			CheckClassAdapter.verify(cr, true, new PrintWriter(System.out));
			// cr.accept(new TraceClassVisitor(null, new ASMifier(), new
			// PrintWriter("tmp.txt")), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
