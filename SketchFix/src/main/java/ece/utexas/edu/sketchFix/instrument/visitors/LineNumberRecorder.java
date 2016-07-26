/**
 * @author Lisa Jul 16, 2016 LineNumberRecorder.java 
 */
package ece.utexas.edu.sketchFix.instrument.visitors;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import ece.utexas.edu.sketchFix.instrument.InstrumentUtility;
@Deprecated
/**
 * Merge to StateRecorder
 * @author lisahua
 *
 */
public class LineNumberRecorder {
	private static PrintWriter writer = null;
	private static String traceFile = "";

	private LineNumberRecorder() {

	}

	public static void setTraceFile(String file) {
		traceFile = file;
	}

	public static void _sketchFix_recordLine(String line) {
		if (writer == null) {
			if (traceFile.equals(""))
				traceFile = InstrumentUtility.lineNumberTraceFile;
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
