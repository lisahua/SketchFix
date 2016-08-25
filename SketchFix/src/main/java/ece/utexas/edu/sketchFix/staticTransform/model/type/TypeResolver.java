/**
 * @author Lisa Aug 3, 2016 TypeResolver.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.type;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ece.utexas.edu.sketchFix.staticTransform.model.FieldWrapper;
import ece.utexas.edu.sketchFix.staticTransform.model.MethodWrapper;
import sketch.compiler.ast.core.exprs.ExprNamedParam;

public class TypeResolver {

	HashMap<String, String> importFiles = new HashMap<String, String>();
	HashMap<String, FieldWrapper> fieldMap = new HashMap<String, FieldWrapper>();
	HashMap<String, HashMap<String, MethodWrapper>> methodMap = new HashMap<String, HashMap<String, MethodWrapper>>();
	// HashMap<String, HashSet<List<String>>> constructors = new HashMap<String,
	// HashSet<List<String>>>();

	public TypeResolver(List<ImportDeclaration> imports, TypeDeclaration clazz, String[] dir) {
		initType(clazz);
		for (ImportDeclaration iDecl : imports) {
			String path = iDecl.getName().toString().replace(".", "/") + ".java";
			File code = new File(path);
			for (String d : dir) {
				code = new File(d + path);
				if (code.exists())
					break;
			}

			String filePath = iDecl.getName().toString();
			if (!code.exists()) {
				// System.out.println("[TypeResolver cannot find]" + dir + "," +
				// path);
			} else
				importFiles.put(filePath.substring(filePath.lastIndexOf(".") + 1), code.getAbsolutePath());
		}
	}

	private void initType(TypeDeclaration clazz) {
		parseType(clazz);
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
		if (methodMap.containsKey(type) && methodMap.get(type).containsKey(method)) {
			return methodMap.get(type).get(method);
		}

		if (methodMap.containsKey(type)) {
			HashSet<MethodWrapper> overload = new HashSet<MethodWrapper>();
			String mName = method;
			if (method.contains("_"))
				mName = mName.substring(0, mName.indexOf("_"));
			for (MethodWrapper mtdWrap : methodMap.get(type).values()) {
				String name = mtdWrap.getMethodName();
				if (name.contains("_")) {
					name = name.substring(0, name.indexOf("_"));
					// FIXME I know its hacky
					if (name.equals(mName)) {
						// MethodWrapper wrap = new MethodWrapper(type, method);
						// wrap.setReturnType(mtdWrap.getReturnType());
						// return wrap;
						overload.add(mtdWrap);
						// return mtdWrap;
					}

				}
			}
			if (overload.size() == 1)
				return overload.iterator().next();
			if (overload.size() > 1) {
				Iterator<MethodWrapper> mItr = overload.iterator();
				while (mItr.hasNext()) {
					MethodWrapper wrap = mItr.next();
					String[] realTokens = method.split("_");
					if (wrap.getParamList().size() != realTokens.length - 1)
						continue;
					List<String> paraStrings = wrap.getParamList();
					for (int i = 0; i < paraStrings.size(); i++) {
						if (!matchType(paraStrings.get(i), realTokens[i + 1]))
							continue;
					}
					return wrap;
				}
			}

		}

		MethodWrapper wrap = new MethodWrapper(type, method);
		HashMap<String, MethodWrapper> methods = methodMap.containsKey(type) ? methodMap.get(type)
				: new HashMap<String, MethodWrapper>();
		methods.put(method, wrap);
		methodMap.put(type, methods);

		return wrap;
	}

	private boolean matchType(String declType, String realType) {
		if (declType.equals(realType))
			return true;
		else if (realType.equals("int"))
			return declType.equals("bit") || declType.equals("boolean");
		else if (realType.equals("char"))
			return declType.equals("String");
		else if (realType.equals("null"))
			return !declType.equals("bit") && !declType.equals("int") && declType.equals("double");
		return false;
	}

	// private List<String> getConstructors(String type, List<String> types) {
	// if (!constructors.containsKey(type)) {
	// if (importFiles.containsKey(type)) {
	// try {
	// parseFile(new File(importFiles.get(type)));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// if (constructors.containsKey(type)) {
	// HashSet<List<String>> consts = constructors.get(type);
	// for (List<String> params : consts) {
	// if (params.size() != types.size())
	// continue;
	// boolean flag = true;
	// for (int i = 0; i < params.size(); i++) {
	// if (params.get(i).equals("null"))
	// continue;
	// else if (params.get(i).equals("bit")) {
	// if (!types.get(i).equals("boolean")) {
	// flag = false;
	// break;
	// }
	// } else if (!params.get(i).equals(types.get(i))) {
	// flag = false;
	// break;
	// }
	// }
	// if (flag == true)
	// return params;
	// }
	// }
	//
	// return null;
	// }

	private void parseFile(File code) throws Exception {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		fileString = FileUtils.readFileToString(code);
		parser.setSource(fileString.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		TypeDeclaration type = (TypeDeclaration) cu.types().get(0);
		parseType(type);
	}

	private void parseType(TypeDeclaration type) {

		// FieldDeclaration[] fields = type.getFields();
		HashMap<String, MethodWrapper> mtdMap = new HashMap<String, MethodWrapper>();
		for (MethodDeclaration mtd : type.getMethods()) {
			// handle overload
			List<SingleVariableDeclaration> params = mtd.parameters();
			String name = mtd.getName().toString();
			for (SingleVariableDeclaration para : params) {
				name += "_" + para.getType().toString();
			}
			mtdMap.put(name, new MethodWrapper(type.getName().toString(), mtd));
		}
		methodMap.put(type.getName().toString(), mtdMap);
		fieldMap.put(type.getName().toString(), new FieldWrapper(type));

	}

	public void updateParaType(String classType, String method, int id, String type) {
		MethodWrapper wrap = getMethodWrapper(classType, method);
		if (id == -1) {
			// FIXME invoker mann I dont know how to handle inheritance override
			// now
		} else if (id == 10) {
			// return type
			if (!type.equals(wrap.getReturnType()))
				wrap.setReturnType(type);
		} else {
			wrap.updateParam(id, type);
		}
		HashMap<String, MethodWrapper> methods = methodMap.containsKey(classType) ? methodMap.get(classType)
				: new HashMap<String, MethodWrapper>();
		methods.put(method, wrap);
		methodMap.put(classType, methods);

	}

	public MethodWrapper validateParams(String type, List<String> types) {
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
			HashSet<MethodWrapper> overload = new HashSet<MethodWrapper>();
			String mName = type;
			for (MethodWrapper mtdWrap : methodMap.get(type).values()) {
				String name = mtdWrap.getMethodName();
				if (name.contains("_")) {
					name = name.substring(0, name.indexOf("_"));
					// FIXME I know its hacky
					if (name.equals(mName)) {
						overload.add(mtdWrap);
					}

				}
			}
			if (overload.size() == 1)
				return overload.iterator().next();
			if (overload.size() > 1) {
				Iterator<MethodWrapper> mItr = overload.iterator();
				while (mItr.hasNext()) {
					MethodWrapper wrap = mItr.next();
					if (wrap.getParamList().size() != types.size())
						continue;
					List<String> paraStrings = wrap.getParamList();
					for (int i = 0; i < paraStrings.size(); i++) {
						if (!matchType(paraStrings.get(i),types.get(i)))
							continue;
					}
					return wrap;
				}
			}

		}
//		String method = type;
//		for (String t: types)
//			method += "_"+t;
//		MethodWrapper wrap = new MethodWrapper(type, method);
//		HashMap<String, MethodWrapper> methods = methodMap.containsKey(type) ? methodMap.get(type)
//				: new HashMap<String, MethodWrapper>();
//		methods.put(method, wrap);
//		methodMap.put(type, methods);
		
		return null;
	}

	public List<String> validateFunCallParam(String type, List<String> skParam) {
		if (!methodMap.containsKey(type)) {
			if (importFiles.containsKey(type)) {
				try {
					parseFile(new File(importFiles.get(type)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (!methodMap.containsKey(type))
			return skParam;
		HashMap<String, MethodWrapper> methods = methodMap.get(type);
		for (String mtdName : methods.keySet()) {
			if (mtdName.startsWith(type)) {
				// do sth
			}
		}
		return skParam;
	}
}
