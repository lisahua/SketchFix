package ece.utexas.edu.sketchFix.instrument;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import ece.utexas.edu.sketchFix.instrument.pass1.LineNumberClassVisitor;

public class Instrumenter {
    public static void main(final String args[]) throws Exception {
        FileInputStream is = new FileInputStream("target/test-classes/ece/utexas/edu/sketchFix/instrument/Test.class");
        byte[] b;

        ClassReader cr = new ClassReader(is);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        LineNumberClassVisitor cv = new LineNumberClassVisitor(cw);
        cr.accept(cv, 0);
        b = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream("src/a.txt");
        fos.write(b);
        fos.close();
    }
}



