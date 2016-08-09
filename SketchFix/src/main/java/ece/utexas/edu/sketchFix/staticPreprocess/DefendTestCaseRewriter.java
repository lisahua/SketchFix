/**
 * @author Lisa Aug 8, 2016 DefendTestCaseRewriter.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class DefendTestCaseRewriter {

	public File getTestCase(File read, String path) throws Exception {
		File dir = new File(path.substring(0, path.lastIndexOf("/")));
		if (!dir.exists()) {
			dir.mkdirs();
		}

		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(read));
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
			if (line.trim().startsWith("package") && line.trim().endsWith(";")) {
				sb.append(
						"import com.fasterxml.jackson.annotation.JsonIdentityInfo;\nimport com.fasterxml.jackson.annotation.ObjectIdGenerators;\n");
			}
		}
		reader.close();
		PrintWriter writer = new PrintWriter(path);
		writer.print(sb);
		writer.close();
		return new File(path);
	}

}
