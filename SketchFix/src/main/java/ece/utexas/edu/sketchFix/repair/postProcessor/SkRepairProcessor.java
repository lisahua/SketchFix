/**
 * @author Lisa Aug 20, 2016 SkRepairProcessor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;

public class SkRepairProcessor {
	SketchRepairDeltaMapper mapper;

	public SkRepairProcessor(RepairItem repairItem) {
		List<ASTLinePy> lineStates = new ArrayList<ASTLinePy>();
		lineStates.addAll(repairItem.getStateList());
		mapper = new SketchRepairDeltaMapper(lineStates, repairItem.getBeforeRepair());
	}

	public SketchToDOMTransformer setScope(List<SkLinePy> scope) {
		return mapper.setNewScope(scope);
	}

}
