package ece.utexas.edu.sketchFix.instrument;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")

public class TestReadClassVisitor {
@Test
	public  void main() throws Exception {
		String[] arg = { "--srcDir", "/Users/lisahua/Documents/lisa/project/build/Chart15_buggy/build/",
				"--instrumentDir", "/Users/lisahua/Documents/lisa/project/build/Chart15_buggy/.classes_instrumented/" };
		new InstrumentModel(arg).instrumentCode();
		// ClassReader cr = new ClassReader(is);
		// ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		// cr.accept(new InstrumentClassVisitor(cw), 0);
		// cr.accept(new CheckClassAdapter(cw), 0);
		//
		// final byte[] b = cw.toByteArray();
		// CheckClassAdapter.verify(new ClassReader(b), true, new
		// PrintWriter("tmp.txt"));
		////
		// FileOutputStream fos = new FileOutputStream("Here.class");
		// fos.write(cw.toByteArray());
		// fos.close();
		// is = new FileInputStream("Here.class");
		// cr = new ClassReader(is);
		// cr.accept(new TraceClassVisitor(null, new ASMifier(), new
		// PrintWriter(System.out)), 0);

	}
}