/**
 * @author Lisa Jul 26, 2016 RETURNInstrPy.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState.instrModel;

public class GETFIELDInstrPy extends InstrPy {

	public GETFIELDInstrPy(String line) {
		super(line);
		String[] tkn1 = line.split(":");
		int i = tkn1.length - 2;
		String inst = tkn1[i].trim();
		String[] tokens = inst.split(" ");
		instType = tokens[0];
		if (tokens.length > 1) {
			instSecond = inst.substring(inst.indexOf(" ") + 1);
		}
		varType = tkn1[i + 1].trim();
		if (varType.startsWith("L"))
			varType.substring(1).replace(";", "");
	}

}
