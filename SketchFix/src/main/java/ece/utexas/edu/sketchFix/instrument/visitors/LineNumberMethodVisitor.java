package ece.utexas.edu.sketchFix.instrument.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LineNumberMethodVisitor extends MethodVisitor implements Opcodes {
	private String lineRecorder = LineNumberRecorder.class.getCanonicalName().replace(".", "/");
	private String stateRecorder = StateRecorder.class.getCanonicalName().replace(".", "/");
	private String lineRecordMtd = "_sketchFix_recordLine";
	private String stateRecordMtd = "_sketchFix_recordState";

	// private static int lNo = 0;
	public LineNumberMethodVisitor(MethodVisitor mv) {
		super(ASM5);
		this.mv = mv;
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		mv.visitLineNumber(line, start);
		mv.visitLdcInsn(LineNumberClassVisitor.className + "-" + LineNumberClassVisitor.methodName + "-"
				+ String.valueOf(line));
		mv.visitMethodInsn(INVOKESTATIC, lineRecorder, lineRecordMtd, "(Ljava/lang/String;)V", false);
		mv.visitLdcInsn(LineNumberClassVisitor.className + "-" + LineNumberClassVisitor.methodName + "-"
				+ String.valueOf(line));
		mv.visitMethodInsn(INVOKESTATIC, stateRecorder, lineRecordMtd, "(Ljava/lang/String;)V", false);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		StateRecorder.setTraceFile(
				".build_tests/" + LineNumberClassVisitor.className + "_" + LineNumberClassVisitor.methodName + ".txt");
		mv.visitCode();
		mv.visitVarInsn(opcode, var);
		int op = -1;
		String tmp = "";
		switch (opcode) {
		case ISTORE:
			op = ILOAD;
			tmp = "(I)V";
			break;
		case LSTORE:
			op = LLOAD;
			tmp = "(J)V";
			break;
		case ASTORE:
			op = ALOAD;
			tmp = "(Ljava/lang/Object;)V";
			break;
		case DSTORE:
			op = DLOAD;
			tmp = "(D)V";
			break;
		case FSTORE:
			op = FLOAD;
			tmp = "(F)V";
			break;
		}
		if (op != -1) {
			if (tmp.equals(""))
				tmp = "(Ljava/lang/Object;)V";
			mv.visitVarInsn(op, var);
			try {
				mv.visitMethodInsn(INVOKESTATIC, stateRecorder, stateRecordMtd, tmp, false);
			} catch (Exception e) {
				mv.visitMethodInsn(INVOKESTATIC, stateRecorder, stateRecordMtd, "(Z)V", false);
			}
		}

	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		StateRecorder.setTraceFile(
				".build_tests/" + LineNumberClassVisitor.className + "_" + LineNumberClassVisitor.methodName + ".txt");
		mv.visitCode();
		mv.visitFieldInsn(opcode, owner, name, desc);
		//
		int op = -1;
		String tmp = "";
		switch (opcode) {
		case PUTFIELD:
			op = GETFIELD;
			break;
		case PUTSTATIC:
			op = GETSTATIC;
			break;
		}

		if (op != -1) {
			if (desc.trim().equals("Z"))
				tmp = "(Z)V";
			else if (desc.trim().equals("I"))
				tmp = "(I)V";
			else if (desc.trim().equals("D"))
				tmp = "(D)V";
			else if (desc.trim().equals("F"))
				tmp = "(F)V";
			else if (desc.trim().equals("J"))
				tmp = "(J)V";
			if (tmp.equals(""))
				tmp = "(Ljava/lang/Object;)V";
			if (!owner.trim().equals(""))
				return;
			mv.visitFieldInsn(op, owner, name, desc);

			try {
				mv.visitMethodInsn(INVOKESTATIC, stateRecorder, stateRecordMtd, tmp, false);
			} catch (Exception e) {
				mv.visitMethodInsn(INVOKESTATIC, stateRecorder, stateRecordMtd, "(Z)V", false);
			}
		}
	}
}
