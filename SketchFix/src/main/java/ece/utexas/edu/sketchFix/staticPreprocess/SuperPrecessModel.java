/**
 * @author Lisa Jul 29, 2016 SuperPrecessModel.java 
 */
package ece.utexas.edu.sketchFix.staticPreprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SuperPrecessModel {
	private HashMap<String, HashSet<String>> parentNoMethods = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> subNoMethods = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> subClasses = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> classMethods = new HashMap<String, HashSet<String>>();
	private HashMap<String, HashSet<String>> ignoreMethods = new HashMap<String, HashSet<String>>();

	public SuperPrecessModel(String ignorePathFile) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ignorePathFile));
			String line = "";
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("::");
				if (tokens.length<2) continue;
				HashSet<String> mtds = ignoreMethods.containsKey(tokens[0]) ? ignoreMethods.get(tokens[0])
						: new HashSet<String>();
				mtds.add(tokens[1]);
				ignoreMethods.put(tokens[0], mtds);
			}
			reader.close();
		} catch (Exception e) {
			System.err.println(".pre_processing.log Bad Format: className::methodName");

		}
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
		collectParentMethods();
	}

	private void getNameInFile(File file) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		String fileString = null;
		try {
			fileString = FileUtils.readFileToString(file);
			parser.setSource(fileString.toCharArray());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			collectSubMethods(cu);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void collectSubMethods(CompilationUnit cu) {
		for (Object td : cu.types()) {
			TypeDeclaration tNode = (TypeDeclaration) td;
			List<Type> superClass = (List<Type>) tNode.superInterfaceTypes();
			List<Type> newList = new ArrayList<Type>();
			newList.addAll(superClass);
			newList.add(tNode.getSuperclassType());

			MethodDeclaration[] methods = tNode.getMethods();
			HashSet<String> mNames = new HashSet<String>();
			for (MethodDeclaration md : methods)
				mNames.add(md.getName().toString());
			String currentC = tNode.getName().toString();
			classMethods.put(currentC, mNames);
			subNoMethods.put(currentC, mNames);
			HashSet<String> sClass = new HashSet<String>();
			for (Type obj : superClass) {
				String key = obj.toString();
				sClass.add(key);
				HashSet<String> mNs = (parentNoMethods.containsKey(key)) ? parentNoMethods.get(key)
						: new HashSet<String>();
				mNs.addAll(mNames);
				parentNoMethods.put(key, mNs);
			}
			subClasses.put(currentC, sClass);
		}
	}

	private void collectParentMethods() {
		Queue<String> queue = new LinkedList<String>();
		for (String clazz : subClasses.keySet()) {
			HashSet<String> parents = subClasses.get(clazz);
			if (parents != null) {
				queue.addAll(parents);
				while (!queue.isEmpty()) {
					String tmp = queue.poll();
					if (subClasses.get(tmp) != null) {
						parents.addAll(subClasses.get(tmp));
						queue.addAll(subClasses.get(tmp));
					}
				}
			}
			subClasses.put(clazz, parents);
		}

		for (String clazz : subClasses.keySet()) {
			HashSet<String> parents = subClasses.get(clazz);
			HashSet<String> methods = classMethods.containsKey(clazz) ? classMethods.get(clazz) : new HashSet<String>();
			for (String prt : parents) {
				if (classMethods.containsKey(prt))
					methods.addAll(classMethods.get(prt));
			}
			subNoMethods.put(clazz, methods);
		}

	}

	private boolean typeContainsSubClass(String type, String getter) {
		if (!parentNoMethods.containsKey(type))
			return false;
		else
			return parentNoMethods.get(type).contains(getter);
	}

	// FIXME this bug cannot be resolved as cannot get all methods in library
	private boolean typeContainsParentClass(String type, String getter) {
		if (!subNoMethods.containsKey(type))
			return false;
		else
			return subNoMethods.get(type).contains(getter);
	}

	public boolean cannotAdd(String type, String getter) {
		return typeContainsParentClass(type, getter) || typeContainsSubClass(type, getter);
	}

	public boolean ignored(String className, String mtdName) {
		if (!ignoreMethods.containsKey(className))
			return false;
		else
			return ignoreMethods.get(className).contains(mtdName);
	}
}
