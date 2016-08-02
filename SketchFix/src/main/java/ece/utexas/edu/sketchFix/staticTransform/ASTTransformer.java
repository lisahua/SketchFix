/**
 * @author Lisa Jul 31, 2016 ASTTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;

public class ASTTransformer {

	public void staticTransform(MethodData method) {
		File code = new File(method.getClassFullPath() + ".java");
		if (!code.exists()) {
			code = new File(LocalizerUtility.baseDir + method.getClassFullPath() + ".java");
			if (!code.exists())
				return;
		}
		try {
			parseFile(code, method);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void parseFile(File code, MethodData method) throws Exception {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		fileString = FileUtils.readFileToString(code);
		parser.setSource(fileString.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
		MethodDeclaration[] methods = type.getMethods();
		FieldDeclaration[] fields = type.getFields();
		MethodDeclaration currentMtd = null;
		for (MethodDeclaration mtd : methods) {
			if (mtd.getName().toString().equals(method.getMethodName())) {
				currentMtd = mtd;
				break;
			}
		}
		if (currentMtd == null)
			return;
		List<LinePy> lines = method.getTouchLinesList();
		List<Statement> statements = (List<Statement>) currentMtd.getBody().statements();
		List<ASTLinePy> astLines  = matchLinePyStatementNode(lines, statements);
		SketchSourceGenerator sketchGenerator = new SketchSourceGenerator();
		sketchGenerator.generate(type, currentMtd, astLines, fields);
	}

	private List<ASTLinePy>  matchLinePyStatementNode(List<LinePy> lines, List<Statement> statements) {
		List<ASTLinePy> astLines = new ArrayList<ASTLinePy>();
		boolean[] stmtMark = new boolean[statements.size()];
		boolean[] lineMark = new boolean[lines.size()];
		int id = 0;
		for (int i = 0; i < statements.size(); i++) {
			Statement stmt = statements.get(i);
			String stmtS = stmt.toString().replace(" ", "").replace("\n", "");
			for (; id < lines.size(); id++) {
				if (lineMark[id] == true)
					continue;
				String key = lines.get(id).getSourceLine().replace(" ", "").replace("\n", "");
				if (stmtS.contains(key)) {
					lineMark[id] = true;
					if (stmtMark[i] == false) {
						ASTLinePy astLine = new ASTLinePy(lines.get(id), stmt);
						astLines.add(astLine);
						stmtMark[i] = true;

					} else {
						astLines.get(i).addLinePy(lines.get(id));
					}
				} else
					break;

			}
			boolean check = true;
			for (boolean mark : lineMark) {
				check = check && mark;
			}
			if (check)
				break;

		}
		return astLines;
	}
	
	private void parseStatements(List<ASTLinePy> astLines, FieldDeclaration[] fields) {
		
	}
}