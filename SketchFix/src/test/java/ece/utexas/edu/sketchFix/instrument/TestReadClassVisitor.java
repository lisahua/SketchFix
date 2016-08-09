package ece.utexas.edu.sketchFix.instrument;

import java.io.FileInputStream;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")

public class TestReadClassVisitor {

	public static void main(String[] args) throws Exception {
		FileInputStream is = new FileInputStream(
				"/Users/lisahua/Documents/lisa/project/build/Chart1_buggy/.classes_instrumented/org/jfree/chart/renderer/category/AbstractCategoryItemRenderer.class");

		ClassReader cr = new ClassReader(is);
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		cr.accept(new LineNumberClassVisitor(cw), 0);
		cr.accept(new CheckClassAdapter(cw), 0);

		final byte[] b = cw.toByteArray();
		CheckClassAdapter.verify(new ClassReader(b), true, new PrintWriter("tmp.txt"));
////
//		FileOutputStream fos = new FileOutputStream("Here.class");
//		fos.write(cw.toByteArray());
//		fos.close();
//		is = new FileInputStream("Here.class");
//		cr = new ClassReader(is);
//		cr.accept(new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out)), 0);

	}
}