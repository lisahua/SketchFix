package ece.utexas.edu.sketchFix.instrument.pass1;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LineNumberClassVisitor extends ClassVisitor implements Opcodes {

	public LineNumberClassVisitor(final ClassVisitor cv) {
		super(ASM5, cv);
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		LineNumberRecorder.recordLine(name);
	}

	public void visitEnd() {
		LineNumberRecorder.flush();
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
			final String[] exceptions) {

		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		return mv == null ? null : new LineNumberVisitor(mv);
	}

}