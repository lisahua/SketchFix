/**
 * @author Lisa Jul 24, 2016 StaticMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.repair.processor.SkLineType;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.stmts.StmtWhile;

public class RepairTransformer {

	String funcName = "";
	String[] params;

	public RepairTransformer(Function func, SkLinePy[] isHole, ASTLinePy astHole) {
		String[] token = func.getName().split("_");
		funcName = token[0];
		if (token.length > 1) {
			params = new String[token.length - 1];
			for (int i = 1; i < token.length; i++)
				params[i - 1] = token[i];
		}
		init(isHole, astHole);
	}

	public RepairTransformer() {

	}

	private void init(SkLinePy[] isHole, ASTLinePy astHole) {
		if (isHole == null || astHole == null)
			return;
		List<Statement> delta = findDelta(isHole);

	}

	private List<Statement> findDelta(SkLinePy[] isHole) {
		SkLinePy newLine = isHole[0];
		SkLinePy oldLine = isHole[1];

		List<Statement> newList = new ArrayList<Statement>();
		List<Statement> oldList = new ArrayList<Statement>();

		if (newLine.getType().equals(SkLineType.STBLOCK))
			newList.addAll(((StmtBlock) newLine.getSkStmt()).getStmts());
		else
			newList.add((Statement) newLine.getSkStmt());
		if (oldLine.getType().equals(SkLineType.STBLOCK))
			oldList.addAll(((StmtBlock) oldLine.getSkStmt()).getStmts());
		else
			oldList.add((Statement) oldLine.getSkStmt());
		List<Statement> newDeltaList = new ArrayList<Statement>();
		List<Statement> oldDeltaList = new ArrayList<Statement>();
		for (Statement oldS : oldList) {
			for (Statement newS : newList) {
				if (oldS instanceof StmtExpr && newS instanceof StmtExpr) {
					if (((StmtExpr) newS).getExpression().toString()
							.equals(((StmtExpr) oldS).getExpression().toString()))
						break;
				} else if (oldS instanceof StmtIfThen && newS instanceof StmtIfThen) {
					if (((StmtIfThen) newS).getCond().toString().equals(((StmtIfThen) oldS).getCond().toString()))
						break;
				} else if (oldS instanceof StmtWhile && newS instanceof StmtWhile) {
					if (((StmtWhile) newS).getCond().toString().equals(((StmtWhile) oldS).getCond().toString()))
						break;
				} else if (oldS instanceof StmtVarDecl && newS instanceof StmtVarDecl) {
					if (((StmtVarDecl) newS).getName(0).toString().equals(((StmtVarDecl) oldS).getName(0).toString()))
						break;
				} else if (oldS instanceof StmtAssert && newS instanceof StmtAssert) {
					if (((StmtAssert) newS).getCond().toString().equals(((StmtAssert) oldS).getCond().toString()))
						break;
				}
				// FIXME i know Icannot add delete
				newDeltaList.add(newS);
			}
		}
		return newDeltaList;
	}

	public boolean matchMethod(MethodDeclaration mtd) {
		return false;
	}

	public String getFuncName() {
		return funcName;
	}
}
