/**
 * @author Lisa Jul 20, 2016 StateMethodVisitor.java 
 */
package ece.utexas.edu.sketchFix.slicing.dynamicSlicing;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;

public class StateMethodNode extends MethodNode implements Opcodes {
	public StateMethodNode(MethodNode mn) {
		super(ASM5);

		DefUseInterpreter interpreter = new DefUseInterpreter();
		FlowAnalyzer<Value> flowAnalyzer = new FlowAnalyzer<Value>(interpreter);
		DefUseAnalyzer analyzer = new DefUseAnalyzer(flowAnalyzer, interpreter);
		try {
			analyzer.analyze(StateClassNode.className, mn);
			Variable[] variables = analyzer.getVariables();
			DefUseChain[] chains = new DepthFirstDefUseChainSearch().search(analyzer.getDefUseFrames(),
					analyzer.getVariables(), flowAnalyzer.getSuccessors(), flowAnalyzer.getPredecessors());
			System.out.println(StateClassNode.className + "-" + StateClassNode.methodName + " contains " + chains.length
					+ " Definition-Use Chains");
			for (int i = 0; i < chains.length; i++) {
				DefUseChain chain = chains[i];
				System.out.println("Instruction " + chain.def + " define variable " + variables[chain.var]);
				System.out.println("Instruction " + chain.use + " uses variable " + variables[chain.var]);
				// There is a path between chain.def and chain.use that not
				// redefine chain.var
			}

		} catch (AnalyzerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public StateMethodNode(int access, String name, String desc, String signature, String[] exceptions) {
		super(access, name, desc, signature, exceptions);
	}
}
