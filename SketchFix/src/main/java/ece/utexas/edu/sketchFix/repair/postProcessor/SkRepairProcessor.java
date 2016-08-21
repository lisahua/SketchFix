/**
 * @author Lisa Aug 20, 2016 SkRepairProcessor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;

public class SkRepairProcessor {
	SkRepairMapper mapper;

	public SkRepairProcessor(List<ASTLinePy> assList, List<ASTLinePy> codeList, List<SkLinePy> beoforeRepair) {
		List<ASTLinePy> lineStates = new ArrayList<ASTLinePy>();
		lineStates.addAll(assList);
		lineStates.addAll(codeList);
		mapper = new SkRepairMapper(lineStates, beoforeRepair);
	}

	public Object setScope(List<SkLinePy> scope) {
		mapper.setNewScope(scope);
		return null;
	}

	private void matchStmts() {

	}
}
