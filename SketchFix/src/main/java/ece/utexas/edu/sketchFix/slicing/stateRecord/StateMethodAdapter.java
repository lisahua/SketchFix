package ece.utexas.edu.sketchFix.slicing.stateRecord;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;

public class StateMethodAdapter extends AnalyzerAdapter implements Opcodes {
	private String recorder = StateRecorder.class.getCanonicalName().replace(".", "/");
	private String funcName = "recordLine";

	public StateMethodAdapter(int api, String owner, int access, String name, String desc, MethodVisitor mv) {
		super(api, owner, access, name, desc, mv);
		this.mv = mv;
		StateRecorder.setTraceFile(
				".build_tests/" + StateClassVisitor.className + "-" + StateClassVisitor.methodName + ".txt");
	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
		super.visitFrame(type, nLocal, local, nStack, stack);
		mv.visitFrame(type, nLocal, local, nStack, stack);
		mv.visitLdcInsn(
				"F " + StateClassVisitor.className + "-" + StateClassVisitor.methodName + "-" + local + "-" + stack);
		mv.visitMethodInsn(INVOKESTATIC, recorder, funcName, "(Ljava/lang/String;)V", false);
	}

	@Override
	public void visitVarInsn(int opcode, int var) {
		super.visitVarInsn(opcode, var);
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
			super.visitVarInsn(op, var);
			super.visitMethodInsn(INVOKESTATIC, recorder, funcName, "(Ljava/lang/Object;)V", false);
		}

	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {

		super.visitFieldInsn(opcode, owner, name, desc);
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
			mv.visitMethodInsn(INVOKESTATIC, recorder, funcName, "(Ljava/lang/Object;)V", false);
		}
	}
}