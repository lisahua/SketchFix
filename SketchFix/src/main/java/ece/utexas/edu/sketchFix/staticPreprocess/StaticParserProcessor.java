/**
 * @author Lisa Jul 24, 2016 StaticParserProcessor.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

public class StaticParserProcessor {
	Argument arg = null;
	SuperPrecessModel superChecker =null;

	public StaticParserProcessor(String[] args) {
		arg = new Argument(args);
		superChecker = new SuperPrecessModel(arg.getIgnorePathFile());
	}

	public void getNameInDir(String dirPath) {

		File folder = new File(dirPath);
		if (!folder.isDirectory())
			getNameInFile(folder);
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
	}

	private void getNameInFile(File file) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		try {
			fileString = FileUtils.readFileToString(file);
			parser.setSource(fileString.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			String path = file.getAbsolutePath().replace(arg.getSrcDir(), arg.getWorkDir());
			File dir = new File(path.substring(0, path.lastIndexOf("/")));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			StringBuilder plain = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null)
				plain.append(line + "\n");
			reader.close();
			PrintWriter writer = new PrintWriter(path);
			PreprocessClassRewriter classVisitor = new PreprocessClassRewriter(superChecker);
			classVisitor.process(file, writer);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void process() {
		superChecker.getNameInDir(arg.getSrcDir());
		getNameInDir(arg.getSrcDir());
	}

}
