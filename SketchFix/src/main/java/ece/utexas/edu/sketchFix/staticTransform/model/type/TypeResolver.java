/**
 * @author Lisa Aug 3, 2016 TypeResolver.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.type;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.slicing.LocalizerUtility;
import ece.utexas.edu.sketchFix.staticTransform.model.FieldWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;

public class TypeResolver {

	HashMap<String, String> importFiles = new HashMap<String,String>();
	HashMap<String, FieldWrapper> fieldMap = new HashMap<String, FieldWrapper>();
	HashMap<String, HashMap<String, MethodWrapper>> methodMap = new HashMap<String, HashMap<String, MethodWrapper>>();

	public TypeResolver(List<ImportDeclaration> imports) {
		for (ImportDeclaration iDecl : imports) {
			String path = iDecl.getName().toString().replace(".", "/") + ".java";
			File code = new File(path);
			if (!code.exists()) {
				code = new File(LocalizerUtility.baseDir + path);
				if (!code.exists())
					code = new File(LocalizerUtility.testDir + path);
				if (!code.exists())
					continue;
			}
			String filePath = iDecl.getName().toString();
			importFiles.put(filePath.substring(filePath.lastIndexOf(".")+1), code.getAbsolutePath());
		}
	}

	public String getFieldType(String type, String field) {
		if (!fieldMap.containsKey(type)) {
			if (importFiles.containsKey(type)) {
				try {
					parseFile(new File(importFiles.get(type)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (fieldMap.containsKey(type)) {
			return fieldMap.get(type).getFieldType(field);
		}
		return "";
	}

	public String getMethodReturnType(String type, String method) {
		if (!methodMap.containsKey(type)) {
			if (importFiles.containsKey(type)) {
				try {
					parseFile(new File(importFiles.get(type)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (methodMap.containsKey(type)) {
			return methodMap.get(type).get(method).getReturnType();
		}
		return "";
	}

	public MethodWrapper getMethodWrapper(String type, String method) {
		if (!methodMap.containsKey(type)) {
			if (importFiles.containsKey(type)) {
				try {
					parseFile(new File(importFiles.get(type)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (methodMap.containsKey(type)) {
			return methodMap.get(type).get(method);
		}
		//TODO if it is inherited from parents, check trace
		return null;
	}

	private void parseFile(File code) throws Exception {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		fileString = FileUtils.readFileToString(code);
		parser.setSource(fileString.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
		// FieldDeclaration[] fields = type.getFields();
		HashMap<String, MethodWrapper> mtdMap = new HashMap<String, MethodWrapper>();
		for (MethodDeclaration mtd : type.getMethods()) {
			mtdMap.put(mtd.getName().toString(), new MethodWrapper(type.getName().toString(),mtd));
		}
		methodMap.put(type.getName().toString(), mtdMap);
		fieldMap.put(type.getName().toString(), new FieldWrapper(type));

	}
}
