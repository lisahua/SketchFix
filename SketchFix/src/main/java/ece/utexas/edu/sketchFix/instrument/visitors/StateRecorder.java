/**
 * @author Lisa Jul 16, 2016 LineNumberRecorder.java 
 */
package ece.utexas.edu.sketchFix.instrument.visitors;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StateRecorder {
	private static FileOutputStream writer = null;
	private static String traceFile = "";
	private static int count = 0;

	private StateRecorder() {

	}

	public static void setTraceFile(String file) {
		traceFile = file;
	}

	public static void _sketchFix_recordState(Object line) {
		count++;
		ObjectMapper mapper = new ObjectMapper();
		if (traceFile.equals(""))
			traceFile = ".trace_state.txt";
		try {
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(line);
			FileWriter fw = new FileWriter(traceFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(count  + "------------");
			out.println(jsonInString);
			out.println("------------");
			out.close();
		} catch (Exception e) {
			System.out.println("Cannot serialize " + count + " " + line);
		}
	}

	public static void _sketchFix_recordLine(String line) {
		if (traceFile.equals(""))
			traceFile = ".trace_state.txt";
		try {
			writer = new FileOutputStream(traceFile, true);
			PrintWriter pw = new PrintWriter(writer);
			pw.println("------------");
			pw.println(line);
			pw.println("------------");
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
