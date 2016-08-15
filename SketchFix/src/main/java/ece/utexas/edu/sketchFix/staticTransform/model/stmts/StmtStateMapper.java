/**
 * @author Lisa Aug 13, 2016 StmtStateMapper.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Statement;

import com.fasterxml.jackson.databind.ObjectMapper;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;

public class StmtStateMapper {
	// HashMap<Statement, String> stmtType = new HashMap<Statement, String>();
	HashMap<String, ASTLinePy> strLine = new HashMap<String, ASTLinePy>();
	ObjectMapper mapper = new ObjectMapper();
	HashMap<String, String> classNames = new HashMap<String, String>();

	public StmtStateMapper(Vector<LinePy> trace, List<LinePy> list, String[] baseDir) {
		try {
			matchFileStmt(baseDir, init(trace, list));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Supposed on atomic stmt should only contains one object state
	 * 
	 * @param vds
	 * @param string
	 */
	public Object insertStmt(Statement stmt, String type) {
		String stmtS = stmt.toString().replace(" ", "").replace("\n", "").replace("\t", "");
		for (String key : strLine.keySet()) {
			if (!stmtS.contains(key))
				continue;
			ASTLinePy item = strLine.get(key);
			String state = item.getStateIfAny();
			System.out.println(stmt + "," + type + "," + item);
			try {
				String ts = classNames.get(type);
				if (state.length() == 0 || ts == null)
					return null;
				Object restore = mapper.readValue(state, Class.forName(ts));
				return restore;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	private TreeMap<Integer, ASTLinePy> init(Vector<LinePy> trace, List<LinePy> list) {
		TreeMap<Integer, ASTLinePy> allMapping = new TreeMap<Integer, ASTLinePy>();
		Iterator<LinePy> traceItr = trace.iterator();
		Iterator<LinePy> touchItr = list.iterator();
		if (!touchItr.hasNext())
			return null;
		LinePy line = touchItr.next();
		boolean flag = false;
		ASTLinePy astLine = null;
		while (traceItr.hasNext()) {
			LinePy item = traceItr.next();
			insertClassDic(item);
			if (item.toString().equals(line.toString())) {
				if (astLine != null)
					allMapping.put(astLine.getLinePyList().get(0).getLineNum(), astLine);
				astLine = new ASTLinePy();
				flag = true;
				if (touchItr.hasNext())
					line = touchItr.next();
			}
			if (flag)
				astLine.addLinePy(item);
		}
		allMapping.put(astLine.getLinePyList().get(0).getLineNum(), astLine);

		return allMapping;
	}

	private void insertClassDic(LinePy item) {
		String file = item.getFilePath();
		String clazz = file.substring(file.lastIndexOf("/") + 1);
		classNames.put(clazz, file.replace("/", "."));
	}

	private void matchFileStmt(String[] baseDir, TreeMap<Integer, ASTLinePy> allMapping) throws Exception {
		File code = null;
		LinePy first = null;
		try {
			for (ASTLinePy line : allMapping.values()) {
				first = line.getLinePyList().get(0);
				break;
			}
		} catch (Exception e) {
			return;
		}
		if (first == null)
			return;
		for (String base : baseDir) {
			code = new File(base + first.getFilePath() + ".java");
			if (code.exists())
				break;
		}
		if (code == null || !code.exists())
			return;
		BufferedReader reader = new BufferedReader(new FileReader(code));
		String line = "";
		int id = 0;
		while ((line = reader.readLine()) != null) {
			id++;
			if (allMapping.containsKey(id))
				allMapping.get(id).setFirstLineString(line);
		}
		reader.close();

		for (ASTLinePy linePy : allMapping.values()) {
			strLine.put(linePy.getLinePyString().replace(" ", "").replace("\n", "").replace("\t", ""), linePy);
		}
	}

	@Deprecated
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
					// currentMtd = mDecl;
					return astLines;
				}
			}

		}
		return null;
	}
}
