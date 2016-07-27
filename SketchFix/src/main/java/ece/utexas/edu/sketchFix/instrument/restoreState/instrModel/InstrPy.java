/**
 * @author Lisa Jul 25, 2016 LinePy.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState.instrModel;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

public class InstrPy {
	private String toString = "";
	// I dont like it, change to enum or invoke asm methods to convert to enum
	String instType = "";
	String varType = "";
	String instSecond = "";
	Object storeState = null;

	public InstrPy(String line) {
		String[] tkn1 = line.split(":");
		int i = tkn1.length - 1;
		if (i > 1) {
			varType = tkn1[i - 1].trim();
		}
		String inst = tkn1[i].trim();
		String[] tokens = inst.split(" ");
		instType = tokens[0];
		if (tokens.length > 1) {
			instSecond = inst.substring(inst.indexOf(" ") + 1);
		}
		toString = line;
	}

	public String toString() {
		return toString;
	}

	public String getToString() {
		return toString;
	}

	public void setToString(String toString) {
		this.toString = toString;
	}

	public String getInstType() {
		return instType;
	}

	public void setInstType(String instType) {
		this.instType = instType;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

	public String getInstSecond() {
		return instSecond;
	}

	public void setInstSecond(String instSecond) {
		this.instSecond = instSecond;
	}

	public Object getStoreState() {
		return storeState;
	}

	public void setStoreState(String stateS, String type) {
		type = type.replace("/", ".");
		try {
			
			Class<?> cName = Class.forName(type);
			ObjectMapper mapper = new ObjectMapper();
			storeState = mapper.readValue(stateS, cName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setStoreState(Object state) {
		this.storeState = state;
	}
}
