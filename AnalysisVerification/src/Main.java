import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import ast.AssertCmd;
import ast.AssertVerifyVisitor;
import ast.Command;

public class Main {

	static Question1 question1;

	public static void main(String[] args) throws FileNotFoundException {
		question1 = new Question1();

		Scanner in = new Scanner(new File("./AnalysisVerification/src/misc/Input.txt")).useDelimiter(" ");
		String[] varList = in.nextLine().split(" "); // first line is the variables
		System.out.println(varList);
		ControlGraph controlGraph = Manager.buildGraph(in);
		System.out.println(chaoticIteration(controlGraph, varList));
	}

	
	// based on the algorithm in lecture 7, page 108
	public static boolean chaoticIteration(ControlGraph controlGraph, String[] varList) {
		// initialization
		controlGraph.start.state = Manager.initializeState(varList,"TOP");
		for (Vertex v : controlGraph.namedVertices.values()) {
			if (v.state == null) {
				v.state = Manager.initializeState(varList,"BOTTOM");
			}
		}

		List<Vertex> workList = new ArrayList<>(controlGraph.namedVertices.values());

		while (!workList.isEmpty()) { // need to check edge case where there is no variable at the beginning?
			Vertex currNode = workList.remove(0);
			Map<String, String> newState = currNode.state;
			
			// the new state of our current vertex is given by join of all vertices point to the vertex, after applying the corresponding abstract function
			for (Entry<Vertex, Command> entry : currNode.pointedBy.entrySet()) {
				newState = question1.join(newState, question1.applyAbstractFunction(entry.getKey().state, entry.getValue()));
			}
			
			if (!newState.equals(currNode.state)) {
				controlGraph.namedVertices.get(currNode.label).state = newState;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo); // problem: inserts existing item!
			}
			// make sure no duplicates in work list
			Set<Vertex> set = new HashSet<>(workList);
			workList.clear();
			workList.addAll(set);
		}
		for (Vertex v : controlGraph.namedVertices.values()) {
			AssertVerifyVisitor verifier = new AssertVerifyVisitor(v.state);
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