import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import ast.*;

public class Main {

	static ParityAnalysis parityAnalysis = new ParityAnalysis();
	static CPAnalysis CPAnalysis = new CPAnalysis();
	static VEAnalysis VEAnalysis = new VEAnalysis();
	static CartesianAnalysis cartesianAnalysis = new CartesianAnalysis();
	static ControlGraph controlGraph;
	static String[] varList;

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Running final project- Program Analysis and Verification");
		Scanner input = new Scanner(System.in);
		System.out.println("Choose which analysis to run (Parity/Sum/Cartesian):");
		String analysisType = input.nextLine();
		System.out.println("Choose which program to run the analysis on (1/2/3/4/5): ");
		String programToRun = input.nextLine();
		input.close();
		Scanner in = null;
		try {
			in = new Scanner(new File("./AnalysisVerification/src/misc/" + analysisType +
					"/" + programToRun + ".txt")).useDelimiter(" ");
		}
		catch (Exception e){
			System.out.println("Make sure your input is legal");
			System.exit(1);
		}

		//Scanner in = new Scanner(new File("./AnalysisVerification/src/misc/Sum/2.txt")).useDelimiter(" ");
		varList = in.nextLine().split(" "); // first line is the list of variables
		controlGraph = Manager.buildGraph(in, varList);
		in.close();
		boolean isValid = true;
		switch (analysisType.toLowerCase()){
			case "parity":
				ParityChaoticIteration();
				break;
			case "sum":
				SummationAnalysis();
				break;
			case "cartesian":
				ParityChaoticIteration();
				SummationAnalysis();
				for (Vertex v : controlGraph.namedVertices.values()) {
					Manager.reduceUntilFixed(v.parityState, v.relationalParityState, v.VEState);
				}
				break;
			default:
				System.out.println("Illegal analysis type");
				System.exit(1);
		}
		isValid = checkAssertions();
		if (isValid)
			System.out.println("The program does not violate the assertions");
		else
			System.out.println("The program violates the assertions");
	}

	private static void removeWLDuplicates(List<Vertex> workList) {
		Set<Vertex> set = new HashSet<>(workList);
		workList.clear();
		workList.addAll(set);
	}

	private static boolean checkAssertions() {
		for (Vertex v : controlGraph.namedVertices.values()) {
			for (Entry<Vertex, Command> entry : v.pointedBy.entrySet()) {
				Command command = entry.getValue();
				if (command instanceof AssertCmd) {
					Vertex vertexToCheck = entry.getKey();
					AssertVerifyVisitor verifier = new AssertVerifyVisitor(vertexToCheck.parityState, vertexToCheck.relationalParityState,
							vertexToCheck.CPState, vertexToCheck.VEState);
					boolean isOk = ((AssertCmd) command).acceptVerifier(verifier);
					if (!isOk) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static void ParityChaoticIteration() {
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
				// newVE = VEAnalysis.join(newVE, v.getNewState());
				newVE.addAll(v.getNewState());
			}

			if (!newVE.equals(currNode.VEState) && currNode.pointedBy.size() > 0) {
				controlGraph.namedVertices.get(currNode.label).VEState = newVE;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo);
			}
			removeWLDuplicates(workList);
		}
	}

	/*public static void CartesianChaoticIteration() {
		List<Vertex> workList = new ArrayList<>(controlGraph.namedVertices.values());
		Set<String> setVars = new HashSet<>();
		setVars.addAll(Arrays.asList(varList)); // convert array to set

		while (!workList.isEmpty()) {
			Vertex currNode = workList.remove(0);
			Map<String, CartesianProduct> newCartesianState = Manager.initializeCartesianState(varList, CartesianVisitor.bottomProduct(setVars));

			// the new state of our current vertex is given by join of all vertices point to the vertex, after applying the corresponding abstract function
			for (Entry<Vertex, Command> entry : currNode.pointedBy.entrySet()) {
				CartesianVisitor v = cartesianAnalysis.applyAbstractFunction(entry.getKey().cartesianState, entry.getValue());
				newCartesianState = CartesianAnalysis.join(newCartesianState, v.getNewState());
			}

			if (!newCartesianState.equals(currNode.cartesianState) && currNode.pointedBy.size() > 0) {
				controlGraph.namedVertices.get(currNode.label).cartesianState = newCartesianState;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo);
			}
			removeWLDuplicates(workList);
		}

	}*/

	public static void SummationAnalysis() {
		CPChaoticIteration();
		VEChaoticIteration();
		for (Vertex v : controlGraph.namedVertices.values()) {
			Manager.reduceUntilFixed(v.CPState, v.VEState);
		}

	}
}