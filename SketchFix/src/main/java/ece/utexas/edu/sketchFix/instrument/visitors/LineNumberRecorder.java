/**
 * @author Lisa Jul 16, 2016 LineNumberRecorder.java 
 */
package ece.utexas.edu.sketchFix.instrument.visitors;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class LineNumberRecorder {
	private static PrintWriter writer = null;
	private static String traceFile = "";

	private LineNumberRecorder() {

	}

	public static void setTraceFile(String file) {
		traceFile = file;
	}

	public static void recordLine(String line) {
		if (writer == null) {
			if (traceFile.equals(""))
				traceFile = ".trace.txt";
			try {

				writer = new PrintWriter(traceFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		writer.println(line);
		writer.flush();
	}

	public static void flush() {
		writer.flush();
	}
}