package ece.utexas.edu.sketchFix.instrument.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LineNumberClassVisitor extends ClassVisitor implements Opcodes {
	private String recorder = LineNumberRecorder.class.getCanonicalName().replace(".", "/");
	private String funcName = "recordLine";
	public static String className = "";
	private boolean flag = false;

	public LineNumberClassVisitor(ClassVisitor cv) {
		super(ASM5);
		this.cv = cv;
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
			final String[] exceptions) {
		LineNumberMethodVisitor mv = new LineNumberMethodVisitor(
				cv.visitMethod(access, name, desc, signature, exceptions));
	
		return mv;
	}

}