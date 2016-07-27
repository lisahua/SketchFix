/**
 * @author Lisa Jul 26, 2016 InstrPyBuilder.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState.instrModel;

public class InstrPyBuilder {

	public InstrPy buildInstr(String line) {
		String[] tkn1 = line.split(":");
		int i = tkn1.length - 1;
		if (i < 0)
			return null;
		if (tkn1[i].endsWith(";")|| tkn1[i].trim().length()==1) {
			// special case for getfield, getstatic
			i--;
		}
		String inst = tkn1[i].trim();
		if (inst.contains(" "))
			inst = inst.substring(0, inst.indexOf(" "));
		if (inst.contains("NEW"))
			return new NEWInstrPy(line);
		else if (inst.contains("STORE"))
			return new STOREInstrPy(line);
		else if (inst.contains("INVOKE"))
			return new INVOKEInstrPy(line);
		else if (inst.contains("RETURN"))
			return new RETURNInstrPy(line);
		else if (inst.contains("GET")||inst.contains("PUT"))
			return new GETFIELDInstrPy(line);
		return new InstrPy(line);
	}

}
