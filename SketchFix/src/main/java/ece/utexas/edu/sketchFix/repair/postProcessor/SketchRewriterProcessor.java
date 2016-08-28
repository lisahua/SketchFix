/**
 * @author Lisa Aug 22, 2016 SketchRewriterProcessor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class SketchRewriterProcessor {
	PrintWriter writer;
	File file;

	public SketchRewriterProcessor(String inputFile) throws Exception {
		file = new File(inputFile + ".java");
		// if (!file.exists()) return;
		String fileName = inputFile.substring(inputFile.lastIndexOf("/") + 1);
		File output = new File("Repair-" + fileName + ".java");
		int i = 0;
		while (output.exists()) {
			output = new File("Repair-" + fileName + (i++) + ".java");
		}
		System.out.println("[Repaired file:]" + output);
		writer = new PrintWriter(output);
	}

	public void process(SketchToDOMTransformer transformer) throws Exception {
		Document document = new Document(FileUtils.readFileToString(file, StandardCharsets.UTF_8));

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(document.get().toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		// AST ast = cu.getAST();
		// ASTRewrite rewriter = ASTRewrite.create(ast);
		TypeDeclaration tNode = (TypeDeclaration) cu.types().get(0);

		for (MethodDeclaration mtd : tNode.getMethods()) {
			RepairPatch transformed = transformer.matchMethod(mtd, cu.getAST());
			if (transformed != null) {
				String str = document.get();
				String replace = transformed.replaceBody(str);
				writer.println(replace);
				writer.flush();
				return;
			}
		}

	}
}
