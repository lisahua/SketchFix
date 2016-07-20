/**
 * @author Lisa Jul 19, 2016 FaultLocalizerStrategy.java 
 */
package ece.utexas.edu.sketchFix.slicing;

import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

public abstract class FaultLocalizerStrategy {

	public abstract List<LineData> locateFaultyLines(List<Vector<String>> negTraces, List<Vector<String>> posTPath) ;
}
