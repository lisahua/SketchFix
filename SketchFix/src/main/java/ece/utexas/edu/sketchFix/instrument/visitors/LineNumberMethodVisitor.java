package ece.utexas.edu.sketchFix.instrument.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import ece.utexas.edu.sketchFix.slicing.stateRecord.StateClassVisitor;
import ece.utexas.edu.sketchFix.slicing.stateRecord.StateRecorder;

public class LineNumberMethodVisitor extends MethodVisitor implements Opcodes {
	private String lineRecorder = LineNumberRecorder.class.getCanonicalName().replace(".", "/");
	private String stateRecorder = StateRecorder.class.getCanonicalName().replace(".", "/");
	private String funcName = "recordLine";

	public LineNumberMethodVisitor(MethodVisitor mv) {
		super(ASM5);
		this.mv = mv;
		StateRecorder.setTraceFile(
				".build_tests/" + StateClassVisitor.className + "-" + StateClassVisitor.methodName + ".txt");
	
	}

	public void visitLineNumber(int line, Label start) {
		mv.visitLineNumber(line, start);
		mv.visitLdcInsn(LineNumberClassVisitor.className + "-" + LineNumberClassVisitor.methodName + "-"
				+ String.valueOf(line));
		mv.visitMethodInsn(INVOKESTATIC, lineRecorder, funcName, "(Ljava/lang/String;)V", false);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		mv.visitVarInsn(opcode, var);
		int op = -1;
		switch (opcode) {
		case ISTORE:
			op = ILOAD;
			break;
		case LSTORE:
			op = LLOAD;
			break;
		case ASTORE:
			op = ALOAD;
			break;
		case DSTORE:
			op = DLOAD;
			break;
		case FSTORE:
			op = FLOAD;
			break;
		}
		if (op != -1) {
			mv.visitVarInsn(op, var);
			mv.visitMethodInsn(INVOKESTATIC, stateRecorder, funcName, "(Ljava/lang/Object;)V", false);
		}

	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {

		mv.visitFieldInsn(opcode, owner, name, desc);
		//
		int op = -1;
		switch (opcode) {
		case PUTFIELD:
			op = GETFIELD;
			break;
		case PUTSTATIC:
			op = GETSTATIC;
			break;
		}
		if (op != -1) {
			mv.visitFieldInsn(op, owner, name, desc);
			mv.visitMethodInsn(INVOKESTATIC, stateRecorder, funcName, "(Ljava/lang/Object;)V", false);
		}
	}
}
