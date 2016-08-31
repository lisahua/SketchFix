/**
 * @author Lisa Aug 20, 2016 SkCandidateGenerator.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.candidates.RepairItem;
import ece.utexas.edu.sketchFix.repair.candidates.RepairReplaceActor;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.staticTransform.SimpleSketchFilePrinter;
import sketch.compiler.ast.core.Program;

public abstract class CandidateTemplate {

	// protected List<SkLinePy> scope;
	protected SkCandidate originCand = null;
	protected List<RepairItem> repairItems = new ArrayList<RepairItem>();

	public CandidateTemplate(SkCandidate generator) {
		// this.prog = generator.prog;
		// scope = generator.beforeRepair;
		originCand = generator;
	}

	public List<SkCandidate> process() {
		 List<SkCandidate> candidates = new ArrayList<SkCandidate>();
		if (repairItems==null) return candidates;
		for (RepairItem item: repairItems) {
			Program prog = (Program) new RepairReplaceActor(item).visitProgram(originCand.getProg());
			if (validateWithSketch(prog)>0) {
				candidates.add( new SkCandidate(prog, originCand));
			}
		}
		return candidates;
	}
	
	public int validateWithSketch(Program updateProg) {
		Process p;
		try {
			// FIXME
			updateProg.accept(new SimpleSketchFilePrinter(originCand.getOutputFile()+"_"));
			System.out.println("[Generate Repair] " + originCand.getOutputFile());
			if (LocalizerUtility.DEBUG) {
				return 0;
			} else {
				p = Runtime.getRuntime().exec("sketch " + originCand.getOutputFile());
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
					// FIXME buggy
					if (line.length() > 0) {
						return -1;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	public List<RepairItem> getRepairItems() {
		return repairItems;
	}

	protected abstract void init();
	// protected abstract boolean hasDone();
}
