/**
 * @author Lisa Aug 20, 2016 SkRepairProcessor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.FENode;

public class SkRepairProcessor {
	List<SkLinePy> scope;
	List<ASTLinePy> lineStates = new ArrayList<ASTLinePy>();
	private FENode addedNode;

	public SkRepairProcessor(List<ASTLinePy> assList, List<ASTLinePy> codeList, FENode feNode) {
		lineStates.addAll(assList);
		lineStates.addAll(codeList);
		addedNode = feNode;
	}

	public Object setScope(List<SkLinePy> scope) {
		this.scope = scope;
		 matchStmts() ;
		return null;
	}
private void matchStmts() {
	
}
}
