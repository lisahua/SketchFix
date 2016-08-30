/**
 * @author Lisa Aug 13, 2016 StmtStateMapper.java 
 */
package ece.utexas.edu.sketchFix.staticTransform.model.stmts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import ece.utexas.edu.sketchFix.instrument.restoreState.LinePy;
import ece.utexas.edu.sketchFix.staticTransform.ASTLinePy;

public class StmtStateMapper {
	// HashMap<Statement, String> stmtType = new HashMap<Statement, String>();
	// HashMap<String, ASTLinePy> strLine = new HashMap<String, ASTLinePy>();
	// ObjectMapper mapper = new ObjectMapper();
	// HashMap<String, String> classNames = new HashMap<String, String>();
//	StateRequest request = new StateRequest();
	TreeMap<Integer, ASTLinePy> allMapping = new TreeMap<Integer, ASTLinePy>();

	public StmtStateMapper(Vector<LinePy> trace, List<LinePy> list, String[] baseDir) {
		try {
			init(trace, list);
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
	public void insertStmt(org.eclipse.jdt.core.dom.Statement stmt, String type, Object skStmt) {
		String stmtS = stmt.toString().replace(" ", "").replace("\n", "").replace("\t", "");
		TreeMap<Integer, ASTLinePy> copyMap = new TreeMap<Integer, ASTLinePy>(allMapping);
		for (int i : allMapping.keySet()) {
			ASTLinePy astLine = allMapping.get(i);
			if (!stmtS.contains(astLine.getLinePyString()))
				continue;
			if (astLine.getStatement()!=null)
				continue;
			// FIXME nested and repeat line
			astLine.setType(type);
	
			astLine.setStatement(stmt);
			astLine.setSkStmt(skStmt);
			copyMap.put(i, astLine);
			break;
		}
		allMapping = copyMap;
	}

	private void init(Vector<LinePy> trace, List<LinePy> list) {
		// TreeMap<Integer, ASTLinePy> allMapping = new TreeMap<Integer,
		// ASTLinePy>();
		Iterator<LinePy> traceItr = trace.iterator();
		Iterator<LinePy> touchItr = list.iterator();
		if (!touchItr.hasNext())
			return;
		LinePy line = touchItr.next();
		boolean flag = false;
		ASTLinePy astLine = null;
		while (traceItr.hasNext()) {
			LinePy item = traceItr.next();
			// insertClassDic(item);//Not sure if necessary
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
		allMapping.put(astLine.getFirstLineNum(), astLine);

		// return allMapping;
	}
	
	public List<ASTLinePy> getLinePyList() {
//		for (int i: allMapping.keySet())
//			System.out.println(i+":"+allMapping.get(i));
		return new ArrayList<ASTLinePy>(allMapping.values());
	}

	// private void insertClassDic(LinePy item) {
	// String file = item.getFilePath();
	// String clazz = file.substring(file.lastIndexOf("/") + 1);
	// classNames.put(clazz, file.replace("/", "."));
	// }
	/*
	 * private void matchFileStmt(String[] baseDir, TreeMap<Integer, ASTLinePy>
	 * allMapping) throws Exception { File code = null; LinePy first = null; try
	 * { for (ASTLinePy line : allMapping.values()) { first =
	 * line.getLinePyList().get(0); break; } } catch (Exception e) { return; }
	 * if (first == null) return; for (String base : baseDir) { code = new
	 * File(base + first.getFilePath() + ".java"); if (code.exists()) break; }
	 * if (code == null || !code.exists()) return; BufferedReader reader = new
	 * BufferedReader(new FileReader(code)); String line = ""; int id = 0; while
	 * ((line = reader.readLine()) != null) { id++; if
	 * (allMapping.containsKey(id)) allMapping.get(id).setFirstLineString(line);
	 * } reader.close();
	 * 
	 * for (ASTLinePy linePy : allMapping.values()) {
	 * strLine.put(linePy.getLinePyString().replace(" ", "").replace("\n",
	 * "").replace("\t", ""), linePy);
	 * 
	 * } }
	 */
}
