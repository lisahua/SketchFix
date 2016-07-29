/**
 * @author Lisa Jul 24, 2016 StaticMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class AtomicClassVisitor {
	ASTRewrite rewriter = null;
	Document document = null;
	AST ast = null;
	PrintWriter writer = null;
	final String paraName = "__tmp__";

	public AtomicClassVisitor(File file, PrintWriter writer) {
		this.writer = writer;
		try {
			document = new Document(FileUtils.readFileToString(file, StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(document.get().toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		AST ast = cu.getAST();
		rewriter = ASTRewrite.create(ast);
		TypeDeclaration td = (TypeDeclaration) cu.types().get(0);
		if (!visit(td))
			writer.println(document.get());
	}

	private boolean visit(TypeDeclaration node) {
		if (node.isInterface())
			return false;
		try {
			init(node);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void init(TypeDeclaration node) throws Exception {

		FieldDeclaration[] fields = node.getFields();
		MethodDeclaration[] methods = node.getMethods();
		HashSet<String> mNameSet = new HashSet<String>();
		for (MethodDeclaration md : methods) {
			String mName = md.getName().toString();
			if (mName.contains("get") || mName.contains("set"))
				mNameSet.add(md.getName().toString());
		}

		HashMap<String, String> getReturnTypes = new HashMap<String, String>();
		HashMap<String, String> setReturnTypes = new HashMap<String, String>();
		for (FieldDeclaration fNode : fields) {
			String fName = getFieldName(fNode);
			char sChar = ';';
			String newFName = fName;
			if (fName.charAt(0) >= 'a' && fName.charAt(0) <= 'z') {
				sChar = (char) (fName.charAt(0) - 'a' + 'A');
				newFName = sChar + fName.substring(1);
			}
			// getter
			if (!mNameSet.contains("get" + newFName)) {
				createGetter(node, "get" + newFName, fName, fNode);
				getReturnTypes.put(newFName, fNode.getType().toString());
			}
			// setter
			if (!mNameSet.contains("set" + newFName)) {
				if (!fNode.toString().contains("final")) {
					createSetter(node, "set" + newFName, fName, fNode);
					setReturnTypes.put(newFName, fNode.getType().toString());
				}
			}

		}
		end(getReturnTypes, setReturnTypes);
	}

	private String getFieldName(FieldDeclaration field) {
		String frag = field.fragments().toString().replace("[", "").replace("]", "");
		if (frag.contains("="))
			frag = frag.substring(0, frag.indexOf("=")).trim();
		String[] token = frag.split(" ");
		return token[token.length - 1];
	}

	@SuppressWarnings("unchecked")
	private void createGetter(TypeDeclaration node, String funcName, String fName, FieldDeclaration field)
			throws Exception {
		MethodDeclaration methodNode = node.getAST().newMethodDeclaration();
		methodNode.setName(methodNode.getAST().newSimpleName(funcName));

		ReturnStatement returnS = methodNode.getAST().newReturnStatement();

		returnS.setExpression(methodNode.getAST().newSimpleName(fName));
		Block block = methodNode.getAST().newBlock();
		List<Statement> stmts = block.statements();
		stmts.add(returnS);
		methodNode.setBody(block);

		methodNode.modifiers().addAll(methodNode.getAST().newModifiers(org.eclipse.jdt.core.dom.Modifier.PUBLIC));
		if (field.modifiers().toString().contains("static"))
			methodNode.modifiers().addAll(methodNode.getAST().newModifiers(org.eclipse.jdt.core.dom.Modifier.STATIC));
		// FIXME hacky to fix it
		// methodNode.setReturnType2( field.getType());
		ListRewrite bodylrw = rewriter.getListRewrite(node, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		bodylrw.insertLast(methodNode, null);

	}

	private void end(HashMap<String, String> fixGetReturn, HashMap<String, String> fixSetReturn) throws Exception {
		TextEdit edits = rewriter.rewriteAST(document, null);
		edits.apply(document);
		String str = document.get();
		PrintWriter tmp = new PrintWriter("tmp.txt");
		tmp.print(str);
		tmp.close();
		for (String funcName : fixGetReturn.keySet())
			str = str.replace("void " + "get" + funcName + "(", fixGetReturn.get(funcName) + " get" + funcName + "(");
		for (String funcName : fixSetReturn.keySet())
			str = str.replace("set" + funcName + "(int", "set" + funcName + "(" + fixSetReturn.get(funcName));

		writer.println(str);
		writer.flush();
	}

	@SuppressWarnings("unchecked")
	private void createSetter(TypeDeclaration node, String funcName, String fName, FieldDeclaration field) {

		AST typeNode = node.getAST();
		MethodDeclaration methodNode = typeNode.newMethodDeclaration();

		SingleVariableDeclaration para = methodNode.getAST().newSingleVariableDeclaration();
		// para.setType(field.getType());
		para.setName(methodNode.getAST().newSimpleName(paraName));
		para.setVarargs(false);
		para.setExtraDimensions(0);

		methodNode.parameters().add(para);
		methodNode.setName(methodNode.getAST().newSimpleName(funcName));
		methodNode.modifiers().addAll(methodNode.getAST().newModifiers(org.eclipse.jdt.core.dom.Modifier.PUBLIC));
		if (field.modifiers().toString().contains("static"))
			methodNode.modifiers().addAll(methodNode.getAST().newModifiers(org.eclipse.jdt.core.dom.Modifier.STATIC));

		Block block = methodNode.getAST().newBlock();
		List<Statement> stmts = block.statements();

		Assignment aStmt = methodNode.getAST().newAssignment();
		aStmt.setLeftHandSide(methodNode.getAST().newSimpleName(fName));
		aStmt.setRightHandSide(methodNode.getAST().newSimpleName(paraName));
		Statement stmt = methodNode.getAST().newExpressionStatement(aStmt);
		stmts.add(stmt);
		methodNode.setBody(block);

		ListRewrite bodylrw = rewriter.getListRewrite(node, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

		bodylrw.insertLast(methodNode, null);
	}

}
