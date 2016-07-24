/**
 * @author Lisa Jul 16, 2016 LineNumberRecorder.java 
 */
package ece.utexas.edu.sketchFix.instrument.visitors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class StateRecorder {
    private static FileOutputStream writer = null;
    private static String traceFile = "";

    private StateRecorder() {

    }

    public static void setTraceFile(String file) {
        traceFile = file;
    }

    public static void recordLine(Object line) {
        if (writer == null) {
            if (traceFile.equals(""))
                traceFile = ".trace_state.txt";
            try {
                writer = new FileOutputStream(traceFile, true);
                ObjectOutputStream out = new ObjectOutputStream(writer);
                out.writeObject(line);
                out.close();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
