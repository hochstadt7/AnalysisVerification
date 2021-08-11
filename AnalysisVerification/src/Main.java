import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import ast.AssertCmd;
import ast.AssertVerifyVisitor;
import ast.Command;
import ast.ParityVisitor;

public class Main {

	static Question1 question1;

	public static void main(String[] args) throws FileNotFoundException {
		question1 = new Question1();

		Scanner in = new Scanner(new File("./AnalysisVerification/src/misc/DiffParity.txt")).useDelimiter(" ");
		String[] varList = in.nextLine().split(" "); // first line is the variables
		ControlGraph controlGraph = Manager.buildGraph(in);
		System.out.println(chaoticIteration(controlGraph, varList));
	}

	// based on the algorithm in lecture 7, page 108
	public static boolean chaoticIteration(ControlGraph controlGraph, String[] varList) {
		// initialization
		controlGraph.start.state = Manager.initializeState(varList,"TOP");
		controlGraph.start.relationalState = Manager.initRelationalState(varList, "TOP");
		for (Vertex v : controlGraph.namedVertices.values()) {
			if (v.state == null) {
				v.state = Manager.initializeState(varList,"BOTTOM");
				v.relationalState = Manager.initRelationalState(varList, "BOTTOM");
			}
		}

		List<Vertex> workList = new ArrayList<>(controlGraph.namedVertices.values());

		while (!workList.isEmpty()) { // need to check edge case where there is no variable at the beginning?
			Vertex currNode = workList.remove(0);
			Map<String, String> newState = Manager.initializeState(varList,"BOTTOM");
			Map<String, Map<String, String>> newDiffs = Manager.initRelationalState(varList, "BOTTOM");
			
			// the new state of our current vertex is given by join of all vertices point to the vertex, after applying the corresponding abstract function
			for (Entry<Vertex, Command> entry : currNode.pointedBy.entrySet()) {
				//Map<String, String> resultingState = question1.applyAbstractFunction(entry.getKey().state, entry.getValue());
				//Map<String, Map<String, String>> resultingDiffs = question1.computeRelations(resultingState);
				ParityVisitor v = question1.applyAbstractFunction(entry.getKey().state,entry.getKey().relationalState, entry.getValue());
				
				//Map<String, Map<String, String>> resultingDiffs = question1.applyAbstractFunctionDiff(entry.getKey().relationalState, entry.getValue());
				newState = question1.join(newState, v.getNewState());
				newDiffs = question1.joinRelState(newDiffs, v.getNewDiff());
			}
			
			if (!newState.equals(currNode.state) && currNode.pointedBy.size() > 0) { // if the regular state doesn't change, then the diffs don't change either
				controlGraph.namedVertices.get(currNode.label).state = newState;
				controlGraph.namedVertices.get(currNode.label).relationalState = newDiffs;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo);
			}

			// make sure no duplicates in work list
			Set<Vertex> set = new HashSet<>(workList);
			workList.clear();
			workList.addAll(set);
		}

		for (Vertex v : controlGraph.namedVertices.values()) {
			AssertVerifyVisitor verifier = new AssertVerifyVisitor(v.state,v.relationalState);
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

}