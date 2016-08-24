/**
 * @author Lisa Jul 16, 2016 LineNumberRecorder.java 
 */
package ece.utexas.edu.sketchFix.instrument.visitors;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

import ece.utexas.edu.sketchFix.instrument.InstrumentUtility;

public class StateRecorder {
	private static FileOutputStream writer = null;
	private static String traceFile = "";
	private static String stateFile = "";
	private static int count = 0;

	private StateRecorder() {

	}

	public static void setTraceFile(String file) {
		traceFile = file;
	}

	public static void _sketchFix_recordState(Object line) {
		if (stateFile.equals(""))
			stateFile = InstrumentUtility.stateFile;
		stateFile = ".trace_state.txt";
		try {
			FileWriter fw = new FileWriter(stateFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			if (line instanceof String) {
				String tmp = (String) line;
				if (isLineNumberRecord(tmp)) {
					out.println(tmp);
					out.close();
					return;
				}
			}
			count++;
			out.println(count + "------------");
			out.flush();

			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(line);
			out.println(jsonInString);
			out.println("------------");
			out.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}

	private static boolean isLineNumberRecord(String line) {
		if (line.contains("/") && line.contains("-")) {
			try {
				Integer.parseInt(line.substring(line.lastIndexOf("-") + 1));
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static void _sketchFix_recordLine(String line) {
		if (traceFile.equals(""))
			traceFile = InstrumentUtility.lineNumberTraceFile;
		traceFile = ".trace.txt";
		try {
			writer = new FileOutputStream(traceFile, true);
			PrintWriter pw = new PrintWriter(writer);
			pw.println(line);
			writer.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void _sketchFix_recordState(int line) {
		_sketchFix_recordState(String.valueOf(line));
	}

	public static void _sketchFix_recordState(boolean line) {
		_sketchFix_recordState(String.valueOf(line));
	}

	public static void _sketchFix_recordState(double line) {
		_sketchFix_recordState(String.valueOf(line));
	}

	public static void _sketchFix_recordState(float line) {
		_sketchFix_recordState(String.valueOf(line));
	}

	public static void _sketchFix_recordState(long line) {
		_sketchFix_recordState(String.valueOf(line));
	}
}
