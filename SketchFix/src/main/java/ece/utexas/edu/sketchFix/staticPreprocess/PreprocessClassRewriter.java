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
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class PreprocessClassRewriter {
	ASTRewrite rewriter = null;
	Document document = null;
//	AST ast = null;
	PrintWriter writer = null;
	final String paraName = "__tmp__";
	SuperPrecessModel sChecker = null;

	public PreprocessClassRewriter(SuperPrecessModel superCheck) {
		this.sChecker = superCheck;
	}

	public void process(File file, PrintWriter writer) {
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

		preventCycleRef(cu);
		TypeDeclaration tNode = (TypeDeclaration) cu.types().get(0);
		if (!visit(tNode)) {
			writer.println(document.get());
		}
		// for (Object td : cu.types()) {
		// TypeDeclaration tNode = (TypeDeclaration) td;
		// if (!visit(tNode)) {
		// writer.println(document.get());
		// break;
		// }
		// }
	}

	private void preventCycleRef(CompilationUnit cu) {
		ListRewrite listRewrite = rewriter.getListRewrite(cu, CompilationUnit.TYPES_PROPERTY);
		NormalAnnotation jsonIdAnn = cu.getAST().newNormalAnnotation();
		jsonIdAnn.setTypeName(cu.getAST().newSimpleName("JsonIdentityInfo"));
		// first pair
		TypeLiteral newTypeLiteral = cu.getAST().newTypeLiteral();
		newTypeLiteral.setType(cu.getAST().newSimpleType(cu.getAST().newName("ObjectIdGenerators.IntSequenceGenerator")));
		MemberValuePair generator = cu.getAST().newMemberValuePair();
		generator.setName(cu.getAST().newSimpleName("generator"));
		generator.setValue(newTypeLiteral);
		// second pair
		StringLiteral strLit = cu.getAST().newStringLiteral();
		strLit.setLiteralValue("@id");
		MemberValuePair prop = cu.getAST().newMemberValuePair();
		prop.setName(cu.getAST().newSimpleName("property"));
		prop.setValue(strLit);
		jsonIdAnn.values().add(generator);
		jsonIdAnn.values().add(prop);
		listRewrite.insertBefore(jsonIdAnn, (TypeDeclaration) cu.types().get(0), null);
		// insert import
//		listRewrite = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
//		ImportDeclaration impt = cu.getAST().newImportDeclaration();
//		impt.setName(cu.getAST().newName("com.fasterxml.jackson.annotation.JsonIdentityInfo"));
//		listRewrite.insertAfter(impt, cu.getPackage(), null);
//
//		impt = cu.getAST().newImportDeclaration();
//		impt.setName(cu.getAST().newName("com.fasterxml.jackson.annotation.ObjectIdGenerators"));
//		listRewrite.insertAfter(impt,  (ImportDeclaration)cu.imports().get(0), null);

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
				mNameSet.add(md.getName().toString().toLowerCase());
		}

		HashMap<String, String> getReturnTypes = new HashMap<String, String>();
		HashMap<String, String> setReturnTypes = new HashMap<String, String>();
		for (FieldDeclaration fNode : fields) {
			String type = fNode.getType().toString();
			String fName = getFieldName(fNode);
			char sChar = ';';
			String newFName = fName;
			if (fName.charAt(0) >= 'a' && fName.charAt(0) <= 'z') {
				sChar = (char) (fName.charAt(0) - 'a' + 'A');
				newFName = sChar + fName.substring(1);
			}
			String cName = node.getName().toString();
			// getter
			if (!mNameSet.contains("get" + newFName.toLowerCase()) && !sChecker.cannotAdd(type, "get" + newFName)
					&& !sChecker.ignored(cName, "get" + newFName)) {
				createGetter(node, "get" + newFName, fName, fNode);
				getReturnTypes.put(newFName, fNode.getType().toString());
			}
			// setter
			if (!mNameSet.contains("set" + newFName.toLowerCase()) && !sChecker.cannotAdd(type, "set" + newFName)
					&& !sChecker.ignored(cName, "set" + newFName)) {
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
		if (field.modifiers().toString().contains("transient"))
			return;
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
//		PrintWriter tmp = new PrintWriter("tmp.txt");
//		tmp.print(str);
//		tmp.close();
		for (String funcName : fixGetReturn.keySet())
			str = str.replace("void " + "get" + funcName + "(", fixGetReturn.get(funcName) + " get" + funcName + "(");
		for (String funcName : fixSetReturn.keySet())
			str = str.replace("set" + funcName + "(int", "set" + funcName + "(" + fixSetReturn.get(funcName));

		writer.println(str);
		writer.flush();
	}

	@SuppressWarnings("unchecked")
	private void createSetter(TypeDeclaration node, String funcName, String fName, FieldDeclaration field) {
		if (field.modifiers().toString().contains("transient"))
			return;
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
