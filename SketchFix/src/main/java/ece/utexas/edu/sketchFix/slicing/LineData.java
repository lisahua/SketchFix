/**
 d * @author Lisa Jul 19, 2016 LineData.java 
 */
package ece.utexas.edu.sketchFix.slicing;

public class LineData implements Comparable {
	String file;
	int line;
	double suspicious;
	int loc;

	public LineData(String s, int loc, double negRate, double posRate) {
		suspicious = negRate / (negRate + posRate);
		this.loc = loc;

	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	@Override
	public int compareTo(Object o) {
		LineData data = (LineData) o;
		if (data.suspicious > suspicious)
			return 1;
		else
			return -1;
	}

}
