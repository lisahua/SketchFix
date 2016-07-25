/**
 * @author Lisa Jul 16, 2016 LineNumberRecorder.java 
 */
package ece.utexas.edu.sketchFix.instrument.visitors;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class StateRecorder {
	private static FileOutputStream writer = null;
	private static String traceFile = "";
private static int count=0;
	private StateRecorder() {

	}

	public static void setTraceFile(String file) {
		traceFile = file;
	}

	public static void _sketchFix_recordLine(Object line) {
			if (traceFile.equals(""))
				traceFile = ".trace_state.txt";
			try {
				writer = new FileOutputStream(traceFile+(count++), true);
				ObjectOutputStream out = new ObjectOutputStream(writer);
				
				out.writeObject(line);
				
				out.flush();
				writer.flush();
				out.close();
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	public static void _sketchFix_recordLine(int line) {
		_sketchFix_recordLine(String.valueOf(line));
	}

	public static void _sketchFix_recordLine(boolean line) {
		_sketchFix_recordLine(String.valueOf(line));
	}

	public static void _sketchFix_recordLine(double line) {
		_sketchFix_recordLine(String.valueOf(line));
	}

	public static void _sketchFix_recordLine(float line) {
		_sketchFix_recordLine(String.valueOf(line));
	}

	public static void _sketchFix_recordLine(long line) {
		_sketchFix_recordLine(String.valueOf(line));
	}
}
