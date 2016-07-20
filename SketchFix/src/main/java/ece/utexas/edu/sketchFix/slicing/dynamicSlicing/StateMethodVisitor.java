/**
 * @author Lisa Jul 20, 2016 StateMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.slicing.dynamicSlicing;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class StateMethodVisitor extends MethodVisitor implements Opcodes{
	public StateMethodVisitor(MethodVisitor mv) {
		super(ASM5);
		this.mv = mv;
	}
}
