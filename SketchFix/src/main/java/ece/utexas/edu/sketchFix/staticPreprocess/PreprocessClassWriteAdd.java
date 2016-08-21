/**
 * @author Lisa Jul 24, 2016 StaticMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
@Deprecated
public class PreprocessClassWriteAdd {
	private HashMap<String, File> namePathMap = new HashMap<String, File>();
	private String replacedPath = null;
	private final String serString = "java.io.Serializable";

	private void parseDir(File dir) throws Exception {
		if (!dir.isDirectory()) {
			collectFile(dir);
		}

		File[] listOfFiles = dir.listFiles();
		if (listOfFiles == null)
			return;

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].getName().endsWith(".java")) {
				collectFile(listOfFiles[i]);
			} else if (listOfFiles[i].isDirectory()) {
				parseDir(listOfFiles[i]);
			}
		}
	}

	private void parseFile(File file) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		PrintWriter writer = new PrintWriter(file.getAbsolutePath());
		StringBuilder builder = new StringBuilder();
		String fName = file.getName();
		String line = "";
		HashMap<String, String> cannotSer = new HashMap<String, String>();
		boolean flag = false;
		while ((line = reader.readLine()) != null) {
			if (line.trim().startsWith("import")) {
				builder.append(line);
				String impts = line.replace("import", "").replace(";", "").trim();
				if (impts.startsWith("java") || namePathMap.containsKey(impts))
					continue;
				else if (impts.equals(serString))
					return;
				else {
					cannotSer.put(impts.substring(impts.lastIndexOf(".") + 1), impts);
				}
			} else if (line.trim().startsWith("public class " + fName)) {
				flag = true;
				for (char c : line.toCharArray()) {
					builder.append(c);
					if (c == '{') {
						flag = false;
						break;
					}
				}
			} else if (flag == true) {
				for (char c : line.toCharArray()) {
					builder.append(c);
					if (c == '{') {
						flag = false;
						break;
					}
				
				}
			}
		}

	}

	private void collectFile(File file) throws Exception {
		if (replacedPath != null) {
			String path = file.getAbsolutePath().replace("/", ".").replace(replacedPath, "").trim();
			namePathMap.put(path, file);
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line = reader.readLine()) != null) {
			if (line.trim().startsWith("package")) {
				String relPath = line.replace("package", "").replace(";", "").trim();
				String absPath = file.getAbsolutePath().replace("/", ".");
				replacedPath = absPath.substring(0, absPath.indexOf(relPath));
				String path = file.getAbsolutePath().replace("/", ".").replace(replacedPath, "").trim();
				namePathMap.put(path, file);
				reader.close();
				return;
			}
		}
		reader.close();
	}
}