/**
 * @author Lisa Jul 29, 2016 PreProcessorClassRewriter.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class PreProcessorClassRewriter {

	
	private void getNameInFile(File file) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		try {
			fileString = FileUtils.readFileToString(file);
			parser.setSource(fileString.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
AST ast  = cu.getAST();


//			StaticClassVisitor classVisitor = new StaticClassVisitor(writer, plain);
//			cu.accept(classVisitor);
//			classVisitor.removeInnerClass();
//			// StringBuilder sb = classVisitor.getNewFile();
//			// writer.println(sb);
//			writer.close();
			// copyToWorkDir(file, sb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
