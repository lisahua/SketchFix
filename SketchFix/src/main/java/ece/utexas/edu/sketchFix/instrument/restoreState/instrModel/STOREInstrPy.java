/**
 * @author Lisa Jul 26, 2016 RETURNInstrPy.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState.instrModel;

public class STOREInstrPy extends InstrPy {

	public STOREInstrPy(String line) {
		super(line);
	}
	public void setStoreState(String stateS, String varType) {
		String instType = this.instType;
		stateS = stateS.replace("\"", "");
		if (instType.equals("ISTORE")) {
			storeState = Integer.parseInt(stateS);
		} else if (instType.equals("DSTORE")) {
			storeState = Double.parseDouble(stateS);
		} else if (instType.equals("FSTORE"))
			storeState = Float.parseFloat(stateS);
		else if (instType.equals("LSTORE"))
			storeState = Long.parseLong(stateS);
		else if (instType.equals("ASTORE")) {
			super.setStoreState(stateS, varType);
		}
	}
}
