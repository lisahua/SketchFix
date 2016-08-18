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
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePyGenerator;
import ece.utexas.edu.sketchFix.slicing.localizer.model.MethodData;
import ece.utexas.edu.sketchFix.staticTransform.model.AbstractASTAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodDeclarationAdapter;
import ece.utexas.edu.sketchFix.staticTransform.model.OverloadHandler;
import ece.utexas.edu.sketchFix.staticTransform.model.stmts.StmtStateMapper;
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
	protected LinePyGenerator utility = null;
	protected StmtStateMapper stateMapper = null;
	private Function currMethod = null;
	private OverloadHandler overloadHandler = null;

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
		MethodDeclarationAdapter mtdDecl = new MethodDeclarationAdapter(cu, method, utility);
		mtdDecl.setHarness(harness);
		boolean needOverload = overloadHandler.needOverload(currentMtd);
		if (needOverload)
			mtdDecl.setOverloadHandler(overloadHandler);
		currMethod = (Function) mtdDecl.transform(currentMtd);
		StructDefGenerator generator = new StructDefGenerator(AbstractASTAdapter.getUseRecorder(),
				mtdDecl.getTypeResolver());
		// TODO create structDef correspondingly
		methods.addAll(generator.getMethodMap());
		methods.add(currMethod);
		structs.addAll(generator.getStructDefMap());
		stateMapper = mtdDecl.getStateMapper();
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
		List<String> param = method.getParams();
		// HashSet<MethodDeclaration> overloadMtd = new
		// HashSet<MethodDeclaration>();
		for (MethodDeclaration mtd : methods) {
			if (mtd.getName().toString().equals(method.getMethodName())) {
				// check param
				List<SingleVariableDeclaration> params = mtd.parameters();
				if (params.size() != param.size())
					continue;
				boolean flag = true;
				for (int i = 0; i < params.size(); i++) {
					if (!params.get(i).getType().toString().equals(param.get(i))) {
						flag = false;
						break;
					}
				}
				if (flag == false)
					continue;
				else {
					currentMtd = mtd;
					return;
				}
			}
		}
		// List<LinePy> lines = method.getTouchLinesList();
		// List<Statement> statements = (List<Statement>)
		// currentMtd.getBody().statements();
		// astLines = matchLinePyStatementNode(lines, overloadMtd);
	}

	@Deprecated
	private void writeToFile(String path) {
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

	public List<Function> getMergeMethods() {
		return overloadHandler.getMethods(methods);
	}

	public List<StructDef> getMergeStructs() {
		return overloadHandler.getStructs(structs);
	}

	public StmtStateMapper getStateMapper() {
		return stateMapper;
	}

	public void setMethods(List<Function> methods2) {
		methods.addAll(methods2);

	}

	public void setStructs(List<StructDef> structs2) {
		structs.addAll(structs2);

	}

	@Deprecated
	private void mergeAnotherTransformer(AbstractSketchTransformer transformer) {
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

	@Deprecated
	private void mergeTwoStructs(StructDef one, StructDef two) {
		TStructCreator creator = new TStructCreator(AbstractASTAdapter.getContext2());
		creator.name(one.getName());
		List<String> names = new ArrayList<String>();
		List<Type> types = new ArrayList<Type>();
		for (Map.Entry<String, Type> entry : one.getFieldTypMap()) {
			names.add(entry.getKey());
			types.add(entry.getValue());
		}
		// for (Map.Entry<String, Type> entry : two.getFieldTypMap()) {
		// names.add(entry.getKey());
		// types.add(entry.getValue());
		// }
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

	@Deprecated
	private void mergeTwoMethod(Function one, Function two) {
		if (one.toString().length() <= two.toString().length())
			return;
		for (int i = 0; i < methods.size(); i++) {
			if (methods.get(i).getName().equals(one.getName())) {
				methods.remove(i);
				break;
			}
		}
		methods.add(one);
	}

	public Function getCurrMethod() {
		return currMethod;
	}

	public void setRefTransformer(AbstractSketchTransformer refTransformer) {
		overloadHandler = new OverloadHandler(refTransformer);
	}

}
