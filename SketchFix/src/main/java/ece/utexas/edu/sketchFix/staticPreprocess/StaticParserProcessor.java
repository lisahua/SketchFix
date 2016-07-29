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
import org.eclipse.jdt.core.dom.CompilationUnit;

public class StaticParserProcessor {
	Argument arg = null;

	public StaticParserProcessor(String[] args) {
		arg = new Argument(args);
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
			// StaticClassVisitor classVisitor = new StaticClassVisitor(writer,
			// plain);
			AtomicClassVisitor classVisitor = new AtomicClassVisitor(file, writer);
			// cu.accept(classVisitor);

			writer.close();
			// classVisitor.removeInnerClass();
			// StringBuilder sb = classVisitor.getNewFile();
			// writer.println(sb);
			writer.close();
			// copyToWorkDir(file, sb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void copyToWorkDir(File origin, StringBuilder sb) throws
	// Exception {
	//// String path = origin.getAbsolutePath().replace(arg.getSrcDir(),
	// arg.getWorkDir());
	//// File dir = new File(path.substring(0, path.lastIndexOf("/")));
	//// if (!dir.exists()) {
	//// dir.mkdirs();
	//// }
	//
	// String line = "";
	// BufferedReader reader = new BufferedReader(new FileReader(origin));
	//// PrintWriter writer = new PrintWriter(path);
	// LinkedList<String> lastLines = new LinkedList<String>();
	// int MAX = 3;
	// while ((line = reader.readLine()) != null) {
	// if (line.trim().length() > 0)
	// lastLines.add(line);
	// if (lastLines.size() > MAX) {
	// writer.println(lastLines.poll());
	// }
	// }
	// reader.close();
	// while (!lastLines.isEmpty()) {
	// line = lastLines.pollLast();
	// if (line.trim().contains("}")) {
	// line = line.trim();
	// if (line.indexOf("}") > 0) {
	// writer.println(line.substring(0, line.indexOf("}")));
	// }
	// break;
	// }
	// }
	// while (!lastLines.isEmpty()) {
	// writer.println(lastLines.poll());
	// }
	// writer.println(sb);
	// writer.println("}");
	// writer.close();
	// }

	public void process() {
		getNameInDir(arg.getSrcDir());
	}

}