/**
 * @author Lisa Jul 20, 2016 StateClassVisitor.java 
 */
package ece.utexas.edu.sketchFix.slicing.dynamicSlicing;

import java.util.HashMap;
import java.util.Vector;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import ece.utexas.edu.sketchFix.slicing.LineData;

public class StateClassVisitor extends ClassVisitor implements Opcodes {
	public static String className = "";
	HashMap<String, Vector<LineData>> methodMap = new HashMap<String, Vector<LineData>>();

	public StateClassVisitor(ClassVisitor cv) {
		super(ASM5);
		this.cv = cv;
	}

	public StateClassVisitor(ClassVisitor cv, LineData[] suspLocs) {
		super(ASM5);
		this.cv = cv;
		for (LineData data : suspLocs) {
			String key = data.getClassAndMethod();
			Vector<LineData> value = methodMap.containsKey(key) ? methodMap.get(key) : new Vector<LineData>();
			value.add(data);
			methodMap.put(key, value);
		}
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
		className = name;
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature,
			final String[] exceptions) {
		String key = className + "-" + name;
		MethodVisitor mv = null;
		if (methodMap.containsKey(key))
			mv = new StateMethodVisitor(cv.visitMethod(access, name, desc, signature, exceptions));
		else
			mv = super.visitMethod(access, name, desc, signature, exceptions);
		return mv;
	}

}
