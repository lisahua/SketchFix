/**
 * @author Lisa Aug 22, 2016 SketchRewriterProcessor.java 
 */
package ece.utexas.edu.sketchFix.repair.postProcessor;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;

public class SketchRewriterProcessor {
	File file;
	File output;

	public SketchRewriterProcessor(String inputFile) throws Exception {
		file = new File(inputFile);
		// if (!file.exists()) return;
		String fileName = inputFile.substring(inputFile.lastIndexOf("/") + 1);
		output = new File("Repair-" + fileName );
		int i = 0;
		while (output.exists()) {
			output = new File("Repair-" +(i++)+ fileName  );
		}
	}

	public void process(SketchToDOMTransformer transformer) throws Exception {
		Document document = new Document(FileUtils.readFileToString(file, StandardCharsets.UTF_8));

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(document.get().toCharArray());
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		TypeDeclaration tNode = (TypeDeclaration) cu.types().get(0);

		for (MethodDeclaration mtd : tNode.getMethods()) {
			RepairPatch transformed = transformer.matchMethod(mtd, cu.getAST());
			if (transformed != null) {
				String str = document.get();
				String replace = transformed.replaceBody(str);
				PrintWriter writer = new PrintWriter(output);
				writer.println(replace);
				writer.close();
				System.out.println("[Repaired file:]" + output.getPath());
				return;
			}
			// else {
			// output.delete();
			// }
		}

	}
}
