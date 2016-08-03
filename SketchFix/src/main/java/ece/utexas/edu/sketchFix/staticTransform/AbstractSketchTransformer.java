/**
 * @author Lisa Jul 31, 2016 SketchTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.StructDefGenerator;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;
import sketch.compiler.ast.core.typs.StructDef;

public abstract class AbstractSketchTransformer {

	public abstract void transform(MethodData method, LinePyGenerator utility, List<MethodData> locations);

	protected List<MethodData> locations;
	protected CompilationUnit cu;
	protected List<ASTLinePy> astLines;
	protected MethodDeclaration currentMtd;
	protected TypeDeclaration type;

	protected List<Function> methods = new ArrayList<Function>();
	protected List<StructDef> structs = new ArrayList<StructDef>();

	public void staticTransform(MethodData method, List<MethodData> locations) {
		this.locations = locations;
		File code = new File(method.getClassFullPath() + ".java");
		if (!code.exists()) {
			code = new File(LocalizerUtility.baseDir + method.getClassFullPath() + ".java");
			if (!code.exists())
				code = new File(LocalizerUtility.testDir + method.getClassFullPath() + ".java");
			if (!code.exists())
				return;
		}
		try {
			parseFile(code, method);

		} catch (Exception e) {
			e.printStackTrace();
		}

		MethodDeclarationAdapter mtdDecl = new MethodDeclarationAdapter(type, type.getFields(), astLines);
		Function function = (Function) mtdDecl.transform(currentMtd);
		// TODO create structDef correspondingly
		methods.addAll(StructDefGenerator.createMethods(locations));
		methods.add(function);
		structs.addAll(StructDefGenerator.createStructs());
	}

	@SuppressWarnings("unchecked")
	private void parseFile(File code, MethodData method) throws Exception {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		fileString = FileUtils.readFileToString(code);
		parser.setSource(fileString.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		cu = (CompilationUnit) parser.createAST(null);
		type = (TypeDeclaration) cu.types().get(0);
		MethodDeclaration[] methods = type.getMethods();
		// FieldDeclaration[] fields = type.getFields();
		currentMtd = null;
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
		astLines = matchLinePyStatementNode(lines, statements);
	
	}

	private List<ASTLinePy> matchLinePyStatementNode(List<LinePy> lines, List<Statement> statements) {
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
	// protected Program generate() {
	// MethodDeclarationAdapter mtdDecl = new MethodDeclarationAdapter(type,
	// type.getFields(), astLines);
	// Function function = (Function) mtdDecl.transform(currentMtd);
	// // TODO create structDef correspondingly
	// methods.addAll(StructDefGenerator.createMethods(locations));
	// methods.add(function);
	// structs.addAll(StructDefGenerator.createStructs());
	// Program empty = Program.emptyProgram();
	// sketch.compiler.ast.core.Package pkg = new
	// sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
	// structs, new ArrayList<FieldDecl>(), methods, new
	// ArrayList<StmtSpAssert>());
	// List<sketch.compiler.ast.core.Package> pkgList = new
	// ArrayList<sketch.compiler.ast.core.Package>();
	// pkgList.add(pkg);
	//
	// ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new
	// HashSet<Directive>());
	// return progCreator.create();
	//
	// }

	public void writeToFile(String path) {
		Program empty = Program.emptyProgram();
		sketch.compiler.ast.core.Package pkg = new sketch.compiler.ast.core.Package(empty, AbstractASTAdapter.pkgName,
				structs, new ArrayList<FieldDecl>(), methods, new ArrayList<StmtSpAssert>());
		List<sketch.compiler.ast.core.Package> pkgList = new ArrayList<sketch.compiler.ast.core.Package>();
		pkgList.add(pkg);

		ProgramCreator progCreator = new ProgramCreator(empty, pkgList, new HashSet<Directive>());
		Program prog = progCreator.create();
		try {
			prog.accept(new SimpleSketchFilePrinter(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<Function> getMethods() {
		return methods;
	}

	public void setMethods(List<Function> methods) {
		this.methods.addAll(methods);
	}

	public List<StructDef> getStructs() {
		return structs;
	}

	public void setStructs(List<StructDef> structs) {
		this.structs.addAll(structs);
	}

}
