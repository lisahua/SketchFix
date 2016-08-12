/**
 * @author Lisa Jul 31, 2016 SketchTransformer.java 
 */
package ece.utexas.edu.sketchFix.staticTransform;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StructDefGenerator;
import sketch.compiler.Directive;
import sketch.compiler.ast.core.Annotation;
import sketch.compiler.ast.core.FieldDecl;
import sketch.compiler.ast.core.Function;
import sketch.compiler.ast.core.Program;
import sketch.compiler.ast.core.Program.ProgramCreator;
import sketch.compiler.ast.core.stmts.StmtSpAssert;
import sketch.compiler.ast.core.typs.StructDef;
import sketch.compiler.ast.core.typs.StructDef.TStructCreator;
import sketch.compiler.ast.core.typs.Type;
import sketch.util.datastructures.HashmapList;

public abstract class AbstractSketchTransformer {

	public abstract void transform(MethodData method, LinePyGenerator utility, List<MethodData> locations);

	protected List<MethodData> locations;
	protected CompilationUnit cu;
	protected List<ASTLinePy> astLines;
	protected MethodDeclaration currentMtd;
	protected TypeDeclaration type;

	protected List<Function> methods = new ArrayList<Function>();
	protected List<StructDef> structs = new ArrayList<StructDef>();
	protected boolean harness = false;

	protected void staticTransform(MethodData method, List<MethodData> locations) throws Exception {
		this.locations = locations;
		// FIXME no test method executed...
		if (method == null)
			return;
		File code = new File(method.getClassAbsolutePath());
		if (!code.exists())
			return;
		System.out.println("[Checking suspicious location:]" + method.getClassFullPath());
		parseFile(code, method);
		if (astLines == null)
			return;
		MethodDeclarationAdapter mtdDecl = new MethodDeclarationAdapter(cu, astLines, method.getBaseDirs());
		mtdDecl.setHarness(harness);
		Function function = (Function) mtdDecl.transform(currentMtd);
		StructDefGenerator generator = new StructDefGenerator(mtdDecl.getUseRecorder(), mtdDecl.getTypeResolver());
		// TODO create structDef correspondingly
		methods.addAll(generator.getMethodMap());
		methods.add(function);
		structs.addAll(generator.getStructDefMap());

	}

	protected void setHarness(boolean har) {
		harness = har;
	}

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
		HashSet<MethodDeclaration> overloadMtd = new HashSet<MethodDeclaration>();
		for (MethodDeclaration mtd : methods) {
			if (mtd.getName().toString().equals(method.getMethodName())) {
				overloadMtd.add(mtd);
			}
		}
		List<LinePy> lines = method.getTouchLinesList();
		// List<Statement> statements = (List<Statement>)
		// currentMtd.getBody().statements();
		astLines = matchLinePyStatementNode(lines, overloadMtd);
	}

	private List<ASTLinePy> matchLinePyStatementNode(List<LinePy> lines, HashSet<MethodDeclaration> methods) {

		for (MethodDeclaration mDecl : methods) {
			List<Statement> statements = (List<Statement>) mDecl.getBody().statements();
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
					String key = lines.get(id).getSourceLine().replace("\n", "").replace("\t", "").replace(" ", "");
					if (stmtS.indexOf(key) > -1) {
						lineMark[id] = true;
						if (stmtMark[i] == false) {
							ASTLinePy astLine = new ASTLinePy(lines.get(id), stmt);
							astLines.add(astLine);
							stmtMark[i] = true;

						} else {
							astLines.get(i - 1).addLinePy(lines.get(id));
						}
					} else
						break;

				}
				boolean check = true;
				for (boolean mark : lineMark) {
					check = check && mark;
				}
				if (check) {
					currentMtd = mDecl;
					return astLines;
				}
			}

		}
		return null;
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

	public List<StructDef> getStructs() {
		return structs;
	}

	public void setMethods(List<Function> methods2) {
		methods.addAll(methods2);

	}

	public void setStructs(List<StructDef> structs2) {
		structs.addAll(structs2);

	}

	public void mergeAnotherTransformer(AbstractSketchTransformer transformer) {
		HashMap<String, StructDef> curStruct = new HashMap<String, StructDef>();
		HashMap<String, Function> curMtd = new HashMap<String, Function>();

		for (StructDef strct : structs)
			curStruct.put(strct.getName(), strct);
		for (Function func : methods)
			curMtd.put(func.getName(), func);

		for (StructDef strct : transformer.getStructs()) {
			if (curStruct.containsKey(strct.getName())) {
				mergeTwoStructs(strct, curStruct.get(strct.getName()));
			} else
				structs.add(strct);
		}

		for (Function func : transformer.getMethods()) {
			if (curMtd.containsKey(func.getName())) {
				mergeTwoMethod(func, curMtd.get(func.getName()));
			} else
				methods.add(func);
		}

	}

	private void mergeTwoStructs(StructDef one, StructDef two) {
		TStructCreator creator = new TStructCreator(AbstractASTAdapter.getContext2());
		creator.name(one.getName());
		List<String> names = new ArrayList<String>();
		List<Type> types = new ArrayList<Type>();
		for (Map.Entry<String, Type> entry : one.getFieldTypMap()) {
			names.add(entry.getKey());
			types.add(entry.getValue());
		}
		for (Map.Entry<String, Type> entry : two.getFieldTypMap()) {
			names.add(entry.getKey());
			types.add(entry.getValue());
		}
		HashmapList<String, Annotation> annotations = new HashmapList<String, Annotation>();
		creator.annotations(annotations);
		creator.fields(names, types);
		for (int i = 0; i < structs.size(); i++) {
			if (structs.get(i).getName().equals(one.getName())) {
				structs.remove(i);
				break;
			}
		}
		structs.add(creator.create());
	}

	private void mergeTwoMethod(Function one, Function two) {
		if (one.getBody().toString().length() < two.getBody().toString().length())
			return;
		for (int i = 0; i < methods.size(); i++) {
			if (methods.get(i).getName().equals(one.getName())) {
				methods.remove(i);
				break;
			}
		}
		methods.add(one);
	}

}
