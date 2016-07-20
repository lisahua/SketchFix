package ece.utexas.edu.sketchFix.instrument.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LineNumberMethodVisitor extends MethodVisitor implements Opcodes {
	private String recorder = LineNumberRecorder.class.getCanonicalName().replace(".", "/");
	private String funcName = "recordLine";
	private String className = "";

	public LineNumberMethodVisitor(MethodVisitor mv) {
		super(ASM5);

		this.mv = mv;
	}

	public void visitLineNumber(int line, Label start) {
		mv.visitLineNumber(line, start);
		// mv.visitVarInsn(ALOAD, 1);
		mv.visitLdcInsn(LineNumberClassVisitor.className + "-" + String.valueOf(line));
		mv.visitMethodInsn(INVOKESTATIC, recorder, funcName, "(Ljava/lang/String;)V", false);
		// mv.visitInsn(NOP);
		// mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
		// "Ljava/io/PrintStream;");
		// mv.visitLdcInsn(String.valueOf(line));
		// mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
		// "(Ljava/lang/String;)V", false);
	}

}
