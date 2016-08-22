/**
 * @author Lisa Jul 24, 2016 StaticMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import ece.utexas.edu.sketchFix.repair.processor.SkLinePy;
import ece.utexas.edu.sketchFix.repair.processor.SkLineType;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprConstChar;
import sketch.compiler.ast.core.exprs.ExprConstFloat;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.ExprConstant;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssert;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtExpr;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.stmts.StmtVarDecl;
import sketch.compiler.ast.core.stmts.StmtWhile;

public class RepairTransformer {

	String funcName = "";
	String[] params;
	List<Statement> delta;
	org.eclipse.jdt.core.dom.Statement origDOMStmt;

	// MethodDeclaration mtdDecl ;
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
		delta = findDelta(isHole);
		origDOMStmt = astHole.getStatement();

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

	public MethodDeclaration matchMethod(MethodDeclaration mtd) {
		if (!mtd.getName().toString().equals(funcName))
			return null;
		List<SingleVariableDeclaration> paramList = mtd.parameters();
		if (params.length != paramList.size())
			return null;
		for (int i = 0; i < paramList.size(); i++) {
			if (!paramList.get(i).getType().toString().equals(params[i]))
				return null;
		}
		return buildNewNode(mtd);

	}

	public String getFuncName() {
		return funcName;
	}

	private MethodDeclaration buildNewNode(MethodDeclaration methodNode) {
		Block block = methodNode.getAST().newBlock();
		List<org.eclipse.jdt.core.dom.Statement> stmts = block.statements();
		for (Statement stmt : delta) {
			stmts.add(buildStatement(methodNode, stmt));
		}
		methodNode.setBody(block);
		return methodNode;
	}

	private org.eclipse.jdt.core.dom.Statement buildStatement(MethodDeclaration methodNode, Statement stmt) {
		if (stmt instanceof StmtIfThen)
			return buildStmtIfThen(methodNode, (StmtIfThen) stmt);
		else if (stmt instanceof StmtAssign)
			return buildStmtAssign(methodNode, (StmtAssign) stmt);
		else if (stmt instanceof StmtVarDecl)
			return buildStmtVarDecl(methodNode, (StmtVarDecl) stmt);
		else if (stmt instanceof StmtReturn)
			return buildStmtReturn(methodNode);
		else if (stmt instanceof StmtBlock)
			return buildStmtBlock(methodNode, (StmtBlock) stmt);
		return null;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtIfThen(MethodDeclaration mtd, StmtIfThen ifStmt) {
		Expression expr = ifStmt.getCond();
		org.eclipse.jdt.core.dom.Expression domExpr = buildExpression(mtd, expr);
		IfStatement domIfStmt = mtd.getAST().newIfStatement();
		domIfStmt.setExpression(domExpr);

		org.eclipse.jdt.core.dom.Statement domConsStmt = buildStatement(mtd, ifStmt.getCons());
		domIfStmt.setThenStatement(domConsStmt);

		org.eclipse.jdt.core.dom.Statement domAltStmt = buildStatement(mtd, ifStmt.getAlt());
		domIfStmt.setElseStatement(domAltStmt);

		return domIfStmt;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtBlock(MethodDeclaration mtd, StmtBlock block) {
		Block domBlock = mtd.getAST().newBlock();
		List<org.eclipse.jdt.core.dom.Statement> stmts = domBlock.statements();

		for (Statement stmt : block.getStmts()) {
			stmts.add(buildStatement(mtd, stmt));
		}
		return domBlock;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtAssign(MethodDeclaration mtd, StmtAssign assignStmt) {
		Assignment exprAssign = mtd.getAST().newAssignment();
		exprAssign.setLeftHandSide(buildExpression(mtd, assignStmt.getLHS()));
		exprAssign.setRightHandSide(buildExpression(mtd, assignStmt.getRHS()));
		ExpressionStatement exprStmt = mtd.getAST().newExpressionStatement(exprAssign);
		return exprStmt;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtReturn(MethodDeclaration mtd) {
		ReturnStatement rtnStmt = mtd.getAST().newReturnStatement();
		Type rtnType = mtd.getReturnType2();
		if (rtnType.isPrimitiveType()) {
			PrimitiveType.Code type = ((PrimitiveType) rtnType).getPrimitiveTypeCode();
			if (type == PrimitiveType.BOOLEAN) {
				// FIXME based on pre-stmts
				BooleanLiteral rtnExp = mtd.getAST().newBooleanLiteral(false);
				rtnStmt.setExpression(rtnExp);
			} else if (type == PrimitiveType.INT || type == PrimitiveType.DOUBLE) {
				// FIXME
				NumberLiteral num = mtd.getAST().newNumberLiteral("0");
				rtnStmt.setExpression(num);
			}
		}
		return rtnStmt;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtVarDecl(MethodDeclaration mtd, StmtVarDecl declStmt) {
		VariableDeclarationFragment varfrag = mtd.getAST().newVariableDeclarationFragment();
		SimpleName name = mtd.getAST().newSimpleName(declStmt.getName(0));
		varfrag.setName(name);
		varfrag.setInitializer(buildExpression(mtd, declStmt.getInit(0)));
		VariableDeclarationStatement varStmt = mtd.getAST().newVariableDeclarationStatement(varfrag);
		return varStmt;
	}

	private org.eclipse.jdt.core.dom.Expression buildExpression(MethodDeclaration mtd, Expression exp) {
		if (exp instanceof ExprBinary)
			return buildExprBinary(mtd, (ExprBinary) exp);
		else if (exp instanceof ExprConstant)
			return buildExprConstant(mtd, (ExprConstant) exp);
		else if (exp instanceof ExprNew)
			return buildExprNew(mtd, (ExprNew) exp);
		else if (exp instanceof ExprNullPtr)
			return buildNullPtr(mtd, (ExprNullPtr) exp);
		return null;
	}

	private org.eclipse.jdt.core.dom.Expression buildNullPtr(MethodDeclaration mtd, ExprNullPtr exp) {
		return mtd.getAST().newNullLiteral();
	}

	private org.eclipse.jdt.core.dom.Expression buildExprNew(MethodDeclaration mtd, ExprNew exp) {
		ClassInstanceCreation cInst = mtd.getAST().newClassInstanceCreation();
		Type nType = mtd.getAST().newSimpleType(mtd.getAST().newName(exp.getTypeToConstruct().toString()));
		cInst.setType(nType);
		return cInst;
	}

	private org.eclipse.jdt.core.dom.Expression buildExprConstant(MethodDeclaration mtd, ExprConstant exp) {
		if (exp instanceof ExprConstInt) {
			int value = ((ExprConstInt) exp).getVal();
			return mtd.getAST().newNumberLiteral(String.valueOf(value));
		} else if (exp instanceof ExprConstChar) {
			char c = ((ExprConstChar) exp).toString().charAt(0);
			CharacterLiteral charLit = mtd.getAST().newCharacterLiteral();
			charLit.setCharValue(c);
			return charLit;
		} else if (exp instanceof ExprConstFloat) {
			double value = ((ExprConstFloat) exp).getVal();
			return mtd.getAST().newNumberLiteral(String.valueOf(value));
		}
		return null;
	}

	private org.eclipse.jdt.core.dom.Expression buildExprBinary(MethodDeclaration mtd, ExprBinary exp) {
		InfixExpression infixExpr = mtd.getAST().newInfixExpression();
		infixExpr.setLeftOperand(buildExpression(mtd, exp.getLeft()));
		infixExpr.setRightOperand(buildExpression(mtd, exp.getRight()));
		infixExpr.setOperator(convertOperator(exp.getOp()));
		return infixExpr;
	}

	private Operator convertOperator(int op) {
		switch (op) {
		case ExprBinary.BINOP_EQ:
			return Operator.EQUALS;
		case ExprBinary.BINOP_NEQ:
			return Operator.NOT_EQUALS;
		case ExprBinary.BINOP_ADD:
			return Operator.AND;
		case ExprBinary.BINOP_GE:
			return Operator.GREATER_EQUALS;
		case ExprBinary.BINOP_GT:
			return Operator.GREATER;
		case ExprBinary.BINOP_LE:
			return Operator.LESS_EQUALS;
		case ExprBinary.BINOP_AND:
			return Operator.CONDITIONAL_AND;
		case ExprBinary.BINOP_OR:
			return Operator.CONDITIONAL_OR;
		case ExprBinary.BINOP_SUB:
			return Operator.MINUS;
		}
		return null;
	}

}
