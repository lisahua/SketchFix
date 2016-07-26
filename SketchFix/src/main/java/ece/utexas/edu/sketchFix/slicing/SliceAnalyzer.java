/**
 * @author Lisa Jul 20, 2016 SliceAnalyzer.java 
 */
package ece.utexas.edu.sketchFix.slicing;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.Local;
import br.usp.each.saeg.asm.defuse.ObjectField;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;

public class SliceAnalyzer {
	HashMap<String, Vector<LineData>> methodMap = new HashMap<String, Vector<LineData>>();

	public void analyze(ClassNode cn, List<LineData> suspLocs) throws AnalyzerException {
		initMethodMap(suspLocs);

		List<MethodNode> methods = cn.methods;

		for (int i = 0; i < methods.size(); ++i) {
			String key = cn.name + "-" + methods.get(i).name;
			if (!methodMap.containsKey(key))
				continue;
			DefUseInterpreter interpreter = new DefUseInterpreter();
			FlowAnalyzer<Value> flowAnalyzer = new FlowAnalyzer<Value>(interpreter);
			DefUseAnalyzer analyzer = new DefUseAnalyzer(flowAnalyzer, interpreter);

			analyzer.analyze(cn.name, methods.get(i));
			
			Variable[] variables = analyzer.getVariables();
			DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(analyzer.getDefUseFrames(),
					analyzer.getVariables(), flowAnalyzer.getSuccessors(), flowAnalyzer.getPredecessors());

			for (Variable v : variables) {
				if (v instanceof ObjectField) {
					ObjectField field = (ObjectField) v;
					System.out.println("field " + key + " " + field);
				} else if (v instanceof Local) {
					Local var = (Local) v;
					System.out.println("var " + key + " " +var.var) ;
				}else {
					System.out.println("other " + key + " " +v) ;
				}

			}

			for (DefUseChain chain: chains) {
				System.out.println(key + " " + chain.use + " uses variable " + " " +variables[chain.var]+" "+ chain.toString());
			}
		}
	}

	private void initMethodMap(List<LineData> suspLocs) {

		for (LineData data : suspLocs) {
			String key = data.getClassAndMethod();
			Vector<LineData> value = methodMap.containsKey(key) ? methodMap.get(key) : new Vector<LineData>();
			value.add(data);
			methodMap.put(key, value);
		}
	}
}
