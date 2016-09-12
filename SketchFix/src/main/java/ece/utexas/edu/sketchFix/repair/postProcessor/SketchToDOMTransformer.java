/**
 * @author Lisa Jul 24, 2016 StaticMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
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

import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.exprs.ExprBinary;
import sketch.compiler.ast.core.exprs.ExprConstChar;
import sketch.compiler.ast.core.exprs.ExprConstFloat;
import sketch.compiler.ast.core.exprs.ExprConstInt;
import sketch.compiler.ast.core.exprs.ExprConstant;
import sketch.compiler.ast.core.exprs.ExprNew;
import sketch.compiler.ast.core.exprs.ExprNullPtr;
import sketch.compiler.ast.core.exprs.ExprVar;
import sketch.compiler.ast.core.exprs.Expression;
import sketch.compiler.ast.core.stmts.Statement;
import sketch.compiler.ast.core.stmts.StmtAssign;
import sketch.compiler.ast.core.stmts.StmtBlock;
import sketch.compiler.ast.core.stmts.StmtIfThen;
import sketch.compiler.ast.core.stmts.StmtReturn;
import sketch.compiler.ast.core.stmts.StmtVarDecl;

public class SketchToDOMTransformer {

	String funcName = "";
	String[] params;
	List<Statement> delta;
	org.eclipse.jdt.core.dom.Statement origDOMStmt;
	MethodDeclaration methodDecl;
	AST typeNode;
	// private org.eclipse.jdt.core.dom.Statement comment;

	// MethodDeclaration mtdDecl ;
	public SketchToDOMTransformer(Function func, List<Statement> delta, ASTLinePy astHole) {
		String[] token = func.getName().split("_");
		funcName = token[0];
		if (token.length > 1) {
			params = new String[token.length - 1];
			for (int i = 1; i < token.length; i++)
				params[i - 1] = token[i];
		}
		this.delta = delta;
		if (astHole != null)
			origDOMStmt = astHole.getStatement();
	}
	
	public SketchToDOMTransformer(Function func, SkRepairPatch skPatch) {
		// TODO Auto-generated constructor stub
	}

	public RepairPatch matchMethod(MethodDeclaration mtd, AST node) {
		if (!mtd.getName().toString().equals(funcName))
			return null;
		List<SingleVariableDeclaration> paramList = mtd.parameters();
		if (params.length != paramList.size())
			return null;
		for (int i = 0; i < paramList.size(); i++) {
			if (!paramList.get(i).getType().toString().equals(params[i]))
				return null;
		}
		this.methodDecl = mtd;
		this.typeNode = node;
		// this.comment = comment;
		return buildNewNode(mtd);

	}

	public String getFuncName() {
		return funcName;
	}

	private RepairPatch buildNewNode(MethodDeclaration methodNode) {
		Block block = methodNode.getBody();
		RepairPatch patch = new RepairPatch(block, typeNode);

		List<org.eclipse.jdt.core.dom.Statement> stmts = block.statements();
		if (origDOMStmt == null)
			return patch;
		for (int i = 0; i < stmts.size(); i++) {
			if (stmts.get(i).toString().equals(origDOMStmt.toString())) {
				patch.setInsertPoint(i);
				for (Statement stmt : delta) {
					// patch.insertStatement(comment);
					patch.insertStatement(buildStatement(stmt));
				}
				return patch;
			}
			// newstmts.add(stmts.get(i));
		}
		// BlockComment comment = typeNode.newBlockComment();

		// methodNode.setBody(block);
		return null;
	}

	private org.eclipse.jdt.core.dom.Statement buildStatement(Statement stmt) {
		if (stmt instanceof StmtIfThen)
			return buildStmtIfThen((StmtIfThen) stmt);
		else if (stmt instanceof StmtAssign)
			return buildStmtAssign((StmtAssign) stmt);
		else if (stmt instanceof StmtVarDecl)
			return buildStmtVarDecl((StmtVarDecl) stmt);
		else if (stmt instanceof StmtReturn)
			return buildStmtReturn();
		else if (stmt instanceof StmtBlock)
			return buildStmtBlock((StmtBlock) stmt);
		return null;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtIfThen(StmtIfThen ifStmt) {
		Expression expr = ifStmt.getCond();
		org.eclipse.jdt.core.dom.Expression domExpr = buildExpression(expr);
		IfStatement domIfStmt = typeNode.newIfStatement();
		domIfStmt.setExpression(domExpr);

		org.eclipse.jdt.core.dom.Statement domConsStmt = buildStatement(ifStmt.getCons());
		domIfStmt.setThenStatement(domConsStmt);

		org.eclipse.jdt.core.dom.Statement domAltStmt = buildStatement(ifStmt.getAlt());
		domIfStmt.setElseStatement(domAltStmt);

		return domIfStmt;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtBlock(StmtBlock block) {
		Block domBlock = typeNode.newBlock();
		List<org.eclipse.jdt.core.dom.Statement> stmts = domBlock.statements();

		for (Statement stmt : block.getStmts()) {
			stmts.add(buildStatement(stmt));
		}
		return domBlock;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtAssign(StmtAssign assignStmt) {
		Assignment exprAssign = typeNode.newAssignment();
		exprAssign.setLeftHandSide(buildExpression(assignStmt.getLHS()));
		exprAssign.setRightHandSide(buildExpression(assignStmt.getRHS()));
		ExpressionStatement exprStmt = typeNode.newExpressionStatement(exprAssign);
		return exprStmt;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtReturn() {
		ReturnStatement rtnStmt = typeNode.newReturnStatement();
		Type rtnType = methodDecl.getReturnType2();
		if (rtnType.isPrimitiveType()) {
			PrimitiveType.Code type = ((PrimitiveType) rtnType).getPrimitiveTypeCode();
			if (type == PrimitiveType.BOOLEAN) {
				// FIXME based on pre-stmts
				BooleanLiteral rtnExp = typeNode.newBooleanLiteral(false);
				rtnStmt.setExpression(rtnExp);
			} else if (type == PrimitiveType.INT || type == PrimitiveType.DOUBLE) {
				// FIXME
				NumberLiteral num = typeNode.newNumberLiteral("0");
				rtnStmt.setExpression(num);
			}
		}
		return rtnStmt;
	}

	private org.eclipse.jdt.core.dom.Statement buildStmtVarDecl(StmtVarDecl declStmt) {
		VariableDeclarationFragment varfrag = typeNode.newVariableDeclarationFragment();
		SimpleName name = typeNode.newSimpleName(declStmt.getName(0));
		varfrag.setName(name);
		varfrag.setInitializer(buildExpression(declStmt.getInit(0)));
		VariableDeclarationStatement varStmt = typeNode.newVariableDeclarationStatement(varfrag);
		return varStmt;
	}

	private org.eclipse.jdt.core.dom.Expression buildExpression(Expression exp) {
		if (exp instanceof ExprBinary)
			return buildExprBinary((ExprBinary) exp);
		else if (exp instanceof ExprConstant)
			return buildExprConstant((ExprConstant) exp);
		else if (exp instanceof ExprNew)
			return buildExprNew((ExprNew) exp);
		else if (exp instanceof ExprNullPtr)
			return buildNullPtr((ExprNullPtr) exp);
		else if (exp instanceof ExprVar)
			return buildVar((ExprVar) exp);
		return null;
	}

	private org.eclipse.jdt.core.dom.Expression buildVar(ExprVar exp) {
		return typeNode.newSimpleName(exp.getName());
	}

	private org.eclipse.jdt.core.dom.Expression buildNullPtr(ExprNullPtr exp) {
		return typeNode.newNullLiteral();
	}

	private org.eclipse.jdt.core.dom.Expression buildExprNew(ExprNew exp) {
		ClassInstanceCreation cInst = typeNode.newClassInstanceCreation();
		Type nType = typeNode.newSimpleType(typeNode.newName(exp.getTypeToConstruct().toString()));
		cInst.setType(nType);
		return cInst;
	}

	private org.eclipse.jdt.core.dom.Expression buildExprConstant(ExprConstant exp) {
		if (exp instanceof ExprConstInt) {
			int value = ((ExprConstInt) exp).getVal();
			return typeNode.newNumberLiteral(String.valueOf(value));
		} else if (exp instanceof ExprConstChar) {
			char c = ((ExprConstChar) exp).toString().charAt(0);
			CharacterLiteral charLit = typeNode.newCharacterLiteral();
			charLit.setCharValue(c);
			return charLit;
		} else if (exp instanceof ExprConstFloat) {
			double value = ((ExprConstFloat) exp).getVal();
			return typeNode.newNumberLiteral(String.valueOf(value));
		}
		return null;
	}

	private org.eclipse.jdt.core.dom.Expression buildExprBinary(ExprBinary exp) {
		InfixExpression infixExpr = typeNode.newInfixExpression();
		infixExpr.setLeftOperand(buildExpression(exp.getLeft()));
		infixExpr.setRightOperand(buildExpression(exp.getRight()));
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
