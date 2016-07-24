package ece.utexas.edu.sketchFix.slicing.stateRecord;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class StateClassVisitor extends ClassVisitor implements Opcodes {
    public static String className = "";
    public static String methodName = "";

    public StateClassVisitor(ClassVisitor cv) {
        super(ASM5);
        this.cv = cv;
    }

    @Override
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name,
            final String desc, final String signature,
            final String[] exceptions) {
        methodName = name;
        StateMethodAdapter mv = new StateMethodAdapter(ASM5,className, access, name,desc,
                cv.visitMethod(access, name, desc, signature, exceptions));
        return mv;
    }
    // public FieldVisitor visitField(int access, String name, String desc,
    // String signature, Object value) {
    // return new StateFieldVisitor(ASM5,cv.visitField(access, name, desc,
    // signature, value));
    // }
}