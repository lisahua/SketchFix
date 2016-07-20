package ece.utexas.edu.sketchFix.instrument;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import ece.utexas.edu.sketchFix.instrument.visitors.LineNumberRecorder;

public class TestReadMethodVisitor extends MethodVisitor implements Opcodes {
	private String recorder = LineNumberRecorder.class.getCanonicalName().replace(".", "/");
	private String funcName = "recordLine";

	public TestReadMethodVisitor(MethodVisitor mv) {
		super(ASM5);
		
		this.mv = mv;
	}
//
//	public void visitLineNumber(int line, Label start) {
//		mv.visitLineNumber(line, start);
//        mv.visitVarInsn(ALOAD, 1);
//        mv.visitLdcInsn(String.valueOf(line));
//        mv.visitMethodInsn(INVOKESPECIAL, recorder, funcName,
//                "(Ljava/lang/String;)V", false);
//	}

}
