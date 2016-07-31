/**
 d * @author Lisa Jul 19, 2016 LineData.java 
 */
package ece.utexas.edu.sketchFix.slicing.localizer.model;

public class LineData implements Comparable<LineData> {
	String file;
	int line;
	double suspicious=0.0;
	int loc;
	String method;

	public LineData(String s, int loc, double negRate, double posRate) {
		suspicious = negRate / (negRate + posRate);
		this.loc = loc;
		String[] tkn = s.split("-");
		if (tkn.length > 2) {
			file = tkn[0];
			method = tkn[1];
			// FIXME may throw exception
			line = Integer.parseInt(tkn[2]);
		} else
			file = s;

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

	public double getSuspicious() {
		return suspicious;
	}

	public void setSuspicious(double suspicious) {
		this.suspicious = suspicious;
	}

	public int getLoc() {
		return loc;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	public int compareTo(LineData data) {
		if (data.suspicious > suspicious)
			return 1;
		else
			return 0;
	}

	public String getClassAndMethod() {
		return file + "-" + method;
	}
}
