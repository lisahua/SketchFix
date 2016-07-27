/**
 * @author Lisa Jul 26, 2016 RETURNInstrPy.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState.instrModel;

public class INVOKEInstrPy extends InstrPy {

	public INVOKEInstrPy(String line) {
		super(line);
		if (instType.equals("INVOKESPECIAL") && instSecond.contains("<init>")) {
			varType = instSecond.substring(0, instSecond.indexOf("<init>") - 1);
		} else {
			varType = instSecond.contains(")") ? instSecond.substring(instSecond.indexOf(")") + 1) : varType;
			if (varType.startsWith("L"))
				varType = varType.substring(1).replace(";", "");
		}

	}

}
