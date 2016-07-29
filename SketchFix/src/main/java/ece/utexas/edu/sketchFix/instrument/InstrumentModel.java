/**
 * @author Lisa Jul 16, 2016 InstrumentModel.java 
 */
package ece.utexas.edu.sketchFix.instrument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import ece.utexas.edu.sketchFix.instrument.visitors.InstrumentClassVisitor;

public class InstrumentModel {
	private Arguments args;
	// private CodeInstrumentationTask instrumentationTask;
	private HashSet<File> files = new HashSet<File>();

	public InstrumentModel(String[] arg) {
		args = new Arguments(arg);
//		LineNumberRecorder.setTraceFile(args.getTraceFile());
		cleanUp();
	}

	public InstrumentModel instrumentCode() {

		File base = new File(args.getSrcDir());
		recurAddFile(base);
		for (File file : files) {
			try {

				FileInputStream is = new FileInputStream(file);
				ClassReader cr = new ClassReader(is);
				// ClassNode cn = new ClassNode();
				// cr.accept(new StateClassVisitor(cn), 0);
				ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
				cr.accept(new InstrumentClassVisitor(cw), ClassReader.EXPAND_FRAMES);
				FileOutputStream fos = new FileOutputStream(getInstrumentDir(file));
				fos.write(cw.toByteArray());
				fos.close();

				is = new FileInputStream(getInstrumentDir(file));
				cr = new ClassReader(is);
				FileOutputStream fcos = new FileOutputStream(getInstrumentDir(file) + ".txt", true);
				CheckClassAdapter.verify(cr, true, new PrintWriter(fcos));
			} catch (Exception e) {
				System.out.println("[Tracer Error] " + file.getAbsolutePath());
				e.printStackTrace();
			}
		}
		return this;
	}

	private void recurAddFile(File file) {
		if (!file.isDirectory()) {
			if (file.getName().endsWith(".class"))
				files.add(file);
		} else {
			File[] list = file.listFiles();
			for (File f : list) {
				recurAddFile(f);
			}
		}
	}

	private File getInstrumentDir(File file) {
		String path = file.getAbsolutePath().replace(args.getSrcDir(), args.getInstrumentDir());
		File dir = new File(path.substring(0, path.lastIndexOf("/")));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return new File(path);
	}

	private void cleanUp() {
		File trace = new File(".trace.txt");
		if (trace.exists())
			trace.delete();
		trace = new File(".trace_state.txt");
		if (trace.exists())
			trace.delete();
	}
}
