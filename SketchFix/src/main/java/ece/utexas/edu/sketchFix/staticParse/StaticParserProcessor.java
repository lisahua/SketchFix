/**
 * @author Lisa Jul 24, 2016 StaticParserProcessor.java 
 */
package ece.utexas.edu.sketchFix.staticParse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class StaticParserProcessor {
	private PrintWriter upWriter;

	public StaticParserProcessor() {
	}

	public StaticParserProcessor(String outputPath) {
		try {
			upWriter = new PrintWriter(outputPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void getNameInDir(String dirPath) {

		File folder = new File(dirPath);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null)
			return;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].getName().endsWith(".java")) {
				getNameInFile(listOfFiles[i]);
			} else if (listOfFiles[i].isDirectory()) {
				getNameInDir(listOfFiles[i].getAbsolutePath());
			}
		}
		upWriter.flush();
	}

	public void getNameInFile(File file) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		try {
			fileString = FileUtils.readFileToString(file);
			parser.setSource(fileString.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			StaticClassVisitor classProcessor = new StaticClassVisitor();
			cu.accept(classProcessor);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
