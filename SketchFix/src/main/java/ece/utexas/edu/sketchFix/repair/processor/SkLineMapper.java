/**
 * @author Lisa Aug 20, 2016 SkLineType.java 
 */
package ece.utexas.edu.sketchFix.repair.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import sketch.compiler.ast.core.FEReplacer;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.stmts.StmtWhile;

public class SkLineMapper extends FEReplacer {

	private Vector<String> output;
	List<String> funcs = new ArrayList<String>();
	List<Integer> funcStart = new ArrayList<Integer>();

	private TreeMap<Integer, SkLinePy> lineItems = new TreeMap<Integer, SkLinePy>();

	public SkLineMapper(Vector<String> output) {
		this.output = output;
	}

	public Object visitFunction(Function func) {
		String decl = "void" + func.getName();
		for (int i = 0; i < output.size(); i++) {
			String line = output.get(i).replace(" ", "").replace("\t", "");
//			 System.out.println(decl+"--"+line);
			if (line.indexOf(decl) > -1) {
				funcs.add(func.getName());
				funcStart.add(i);
				lineItems.put(i, new SkLinePy(output.get(i), func, SkLineType.FUNC));
				break;
			}
		}
		return super.visitFunction(func);
	}

	public Object visitStmtAssert(StmtAssert stmt) {
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).contains(stmt.toString())) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt,SkLineType.STASS));
			}
		}
		return super.visitStmtAssert(stmt);
	}

	public Object visitStmtIfThen(StmtIfThen stmt) {
		String line = "if(" + stmt.getCond().toString()+")";
		line = line.replace(" ", "").replace("\t", "");
		for (int i = 0; i < output.size(); i++) {
			String ifLine = output.get(i).replace(" ", "").replace("\t", "");
//			System.out.println(ifLine+"--"+line);
			if (ifLine.indexOf(line)>-1) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt,SkLineType.STIFTHEN));
			}
		}

		return super.visitStmtIfThen(stmt);
	}

	public Object visitStmtVarDecl(StmtVarDecl stmt) {
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).contains(stmt.toString())) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt,SkLineType.STVAR));
			}
		}
		return super.visitStmtVarDecl(stmt);
	}

	public Object visitStmtWhile(StmtWhile stmt) {
		String line = "while(" + stmt.getCond().toString()+")";
		line = line.replace(" ", "").replace("\t", "");
		for (int i = 0; i < output.size(); i++) {
			String ifLine = output.get(i).replace(" ", "").replace("\t", "");
			if (ifLine.contains(line)) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt,SkLineType.STWHILE));
			}
		}
		return super.visitStmtWhile(stmt);
	}

	public Object visitStmtExpr(StmtExpr stmt) {
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).contains(stmt.toString())) {
//				if (!lineItems.containsKey(i))
					lineItems.put(i, new SkLinePy(output.get(i), stmt,SkLineType.STExpr));
			}
		}
		return super.visitStmtExpr(stmt);
	}

	public List<SkLinePy> postProcess() {
		List<SkLinePy> lines = new ArrayList<SkLinePy>();
		int offset = 0;
		for (int i : lineItems.keySet()) {
			int id = lineItems.get(i).getAssLine();
			if (id > 0) {
				offset = i - id;
				break;
			}
		}
		for (int i : lineItems.keySet()) {
			SkLinePy line = lineItems.get(i);
			line.setLineNo(i - offset);
			lines.add(line);
		}
		return lines;
	}
	public List<SkLinePy> getSkLineList() {
		List<SkLinePy> lines = new ArrayList<SkLinePy>();
		
		for (int i : lineItems.keySet()) {
			SkLinePy line = lineItems.get(i);
			line.setLineNo(i);
			lines.add(line);
		}
		return lines;
	}
	
}
