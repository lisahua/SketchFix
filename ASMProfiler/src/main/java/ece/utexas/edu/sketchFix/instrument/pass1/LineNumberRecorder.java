/**
 * @author Lisa Jul 16, 2016 LineNumberRecorder.java 
 */
package ece.utexas.edu.sketchFix.instrument.pass1;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class LineNumberRecorder {
	private static PrintWriter writer = null;

	private LineNumberRecorder() {
		// try {
		// writer = new PrintWriter("tmp.txt");
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public static void recordLine(String line) {
		if (writer == null) {
			try {
				writer = new PrintWriter("tmp.txt");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		writer.println(line);
	}

	public static void flush() {
		writer.flush();
	}
}
