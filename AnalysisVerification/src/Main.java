import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import ast.*;

public class Main {

	static ParityAnalysis parityAnalysis = new ParityAnalysis();
	static CPAnalysis CPAnalysis = new CPAnalysis();
	static VEAnalysis VEAnalysis = new VEAnalysis();
	static ControlGraph controlGraph;
	static String[] varList;

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(new File("./AnalysisVerification/src/misc/ProjectExample.txt")).useDelimiter(" ");
		varList = in.nextLine().split(" "); // first line is the variables
		controlGraph = Manager.buildGraph(in, varList);
		System.out.println(ParityChaoticIteration());
	}

	private static void removeWLDuplicates(List<Vertex> workList) {
		Set<Vertex> set = new HashSet<>(workList);
		workList.clear();
		workList.addAll(set);
	}

	private static boolean checkAssertions() {
		for (Vertex v : controlGraph.namedVertices.values()) {
			AssertVerifyVisitor verifier = new AssertVerifyVisitor(v.parityState, v.relationalParityState, v.CPState, v.VEState);
			for (Entry<Vertex, Command> entry : v.pointedBy.entrySet()) {
				Command command = entry.getValue();
				if (command instanceof AssertCmd) {
					boolean isOk = ((AssertCmd) command).acceptVerifier(verifier);
					if (!isOk) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean ParityChaoticIteration() {
		List<Vertex> workList = new ArrayList<>(controlGraph.namedVertices.values());

		while (!workList.isEmpty()) { // need to check edge case where there is no variable at the beginning?
			Vertex currNode = workList.remove(0);
			Map<String, String> newParity = Manager.initializeParityState(varList, ParityVisitor.BOTTOM);
			Map<String, Map<String, String>> newDiffsParity = Manager.initRelationalParity(varList, ParityVisitor.BOTTOM);

			// the new state of our current vertex is given by join of all vertices point to the vertex, after applying the corresponding abstract function
			for (Entry<Vertex, Command> entry : currNode.pointedBy.entrySet()) {
				ParityVisitor v = parityAnalysis.applyAbstractFunction(entry.getKey().parityState,entry.getKey().relationalParityState, entry.getValue());
				newParity = parityAnalysis.join(newParity, v.getNewState());
				newDiffsParity = parityAnalysis.joinRelState(newDiffsParity, v.getNewDiff());
			}
			
			if (!newParity.equals(currNode.parityState) && currNode.pointedBy.size() > 0) { // if the regular state doesn't change, then the diffs don't change either
				controlGraph.namedVertices.get(currNode.label).parityState = newParity;
				controlGraph.namedVertices.get(currNode.label).relationalParityState = newDiffsParity;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo);
			}
			removeWLDuplicates(workList);
		}
		return checkAssertions();
	}

	public static void CPChaoticIteration() {
		List<Vertex> workList = new ArrayList<>(controlGraph.namedVertices.values());

		while (!workList.isEmpty()) { // need to check edge case where there is no variable at the beginning?
			Vertex currNode = workList.remove(0);
			Map<String, Integer> newCP = Manager.initializeCPState(varList, CPVisitor.BOTTOM);

			// the new state of our current vertex is given by join of all vertices point to the vertex, after applying the corresponding abstract function
			for (Entry<Vertex, Command> entry : currNode.pointedBy.entrySet()) {
				CPVisitor v = CPAnalysis.applyAbstractFunction(entry.getKey().CPState, entry.getValue());
				newCP = CPAnalysis.join(newCP, v.getNewState());
			}

			if (!newCP.equals(currNode.CPState) && currNode.pointedBy.size() > 0) {
				controlGraph.namedVertices.get(currNode.label).CPState = newCP;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo);
			}
			removeWLDuplicates(workList);
		}
	}

	public static void VEChaoticIteration() {
		List<Vertex> workList = new ArrayList<>(controlGraph.namedVertices.values());

		while (!workList.isEmpty()) { // need to check edge case where there is no variable at the beginning?
			Vertex currNode = workList.remove(0);
			Set<VariableEquality> newVE = new HashSet<>(currNode.VEState);

			// the new state of our current vertex is given by join of all vertices point to the vertex, after applying the corresponding abstract function
			for (Entry<Vertex, Command> entry : currNode.pointedBy.entrySet()) {
				VEVisitor v = VEAnalysis.applyAbstractFunction(entry.getKey().VEState, entry.getValue());
				newVE = VEAnalysis.join(newVE, v.getNewState());
			}

			if (!newVE.equals(currNode.VEState) && currNode.pointedBy.size() > 0) {
				controlGraph.namedVertices.get(currNode.label).VEState = newVE;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo);
			}
			removeWLDuplicates(workList);
		}
	}

	public static boolean SummationAnalysis() {
		CPChaoticIteration();
		VEChaoticIteration();
		for (Vertex v : controlGraph.namedVertices.values()) {
			Manager.reduceUntilFixed(v.CPState, v.VEState);
		}
		return checkAssertions();
	}
}