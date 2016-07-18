package ece.utexas.edu.sketchFix.instrument.pass1;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LineNumberVisitor extends MethodVisitor implements Opcodes {


	public LineNumberVisitor(final MethodVisitor mv) {
		super(ASM5, mv);
	}

	public void visitLineNumber(int line, Label start) {
		LineNumberRecorder.recordLine(String.valueOf(line));
	}
	
	
}
