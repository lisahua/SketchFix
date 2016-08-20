/**
 * @author Lisa Aug 20, 2016 SkLineType.java 
 */
package ece.utexas.edu.sketchFix.repair.candidates;

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
	private int funcTmp = 0;
	private TreeMap<Integer, SkLinePy> lineItems = new TreeMap<Integer, SkLinePy>();

	public SkLineMapper(Vector<String> output) {
		this.output = output;
	}

	public Object visitFunction(Function func) {
		String decl = "void" + func.getName();
		for (int i = funcTmp; i < output.size(); i++) {
			String line = output.get(i).replace(" ", "").replace("\t", "");
			if (line.contains(decl)) {
				funcs.add(func.getName());
				funcStart.add(i);
				funcTmp = i;
				lineItems.put(i, new SkLinePy(output.get(i), func));
				break;
			}
		}
		return super.visitFunction(func);
	}

	public Object visitStmtAssert(StmtAssert stmt) {
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).contains(stmt.toString())) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt));
			}
		}
		return super.visitStmtAssert(stmt);
	}

	public Object visitStmtIfThen(StmtIfThen stmt) {
		String line = "if" + stmt.getCond().toString();
		line = line.replace(" ", "").replace("\t", "");
		for (int i = 0; i < output.size(); i++) {
			String ifLine = output.get(i).replace(" ", "").replace("\t", "");
			if (ifLine.contains(line)) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt));
			}
		}

		return super.visitStmtIfThen(stmt);
	}

	public Object visitStmtVarDecl(StmtVarDecl stmt) {
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).contains(stmt.toString())) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt));
			}
		}
		return super.visitStmtVarDecl(stmt);
	}

	public Object visitStmtWhile(StmtWhile stmt) {
		String line = "while" + stmt.getCond().toString();
		line = line.replace(" ", "").replace("\t", "");
		for (int i = 0; i < output.size(); i++) {
			String ifLine = output.get(i).replace(" ", "").replace("\t", "");
			if (ifLine.contains(line)) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt));
			}
		}
		return super.visitStmtWhile(stmt);
	}

	public Object visitStmtExpr(StmtExpr stmt) {
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).contains(stmt.toString())) {
				lineItems.put(i, new SkLinePy(output.get(i), stmt));
			}
		}
		return super.visitStmtExpr(stmt);
	}
}
