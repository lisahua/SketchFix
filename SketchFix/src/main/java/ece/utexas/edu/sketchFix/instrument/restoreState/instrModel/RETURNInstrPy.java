/**
 * @author Lisa Jul 26, 2016 RETURNInstrPy.java 
 */
package ece.utexas.edu.sketchFix.instrument.restoreState.instrModel;

public class RETURNInstrPy extends InstrPy {

	public RETURNInstrPy(String line) {
		super(line);
		
	}
	public void setStoreState(String stateS, String varType) {
		String instType = this.instType;
		stateS = stateS.replace("\"", "");
		if (instType.equals("IRETURN")) {
			storeState = Integer.parseInt(stateS);
		} else if (instType.equals("DRETURN")) {
			storeState = Double.parseDouble(stateS);
		} else if (instType.equals("FRETURN"))
			storeState = Float.parseFloat(stateS);
		else if (instType.equals("LRETURN"))
			storeState = Long.parseLong(stateS);
		else if (instType.equals("ARETURN")) {
			super.setStoreState(stateS, varType);
		}
	}
}
