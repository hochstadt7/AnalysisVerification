import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;
import ast.Command;

public class Main {

	static Question1 question1;

	public static void main(String[] args) throws FileNotFoundException {
		question1 = new Question1();

		Scanner in = new Scanner(new File("./AnalysisVerification/src/Input.txt")).useDelimiter(" ");
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
			v.state = Manager.initializeState(varList,"BOTTOM");
		}

		List<Vertex> workList = new ArrayList<>(controlGraph.namedVertices.values());

		while (!workList.isEmpty()) { // need to check edge case where there is no variable at the beginning?
			Vertex currNode = workList.remove(0);
			Map<String, String> newState = currNode.state;
			
			// the new state of our current vertex is given by join of all vertices point to the vertex, after applying the corresponding abstract function
			for (Entry<Vertex, Command> entry : currNode.pointedBy.entrySet()) {
				newState = question1.join(newState, question1.applyAbstractFunction(entry.getKey().state, entry.getValue()), varList);
			}
			
			if (!newState.equals(currNode.state)) {
				controlGraph.namedVertices.get(currNode.label).state = newState;
				// append all vertices pointed by our current vertex
				workList.addAll(currNode.pointsTo);
			}
			
		}
		for (Vertex v : controlGraph.namedVertices.values()) {
			for (Entry<Vertex, String> entry : v.pointedBy.entrySet()) {
				String command = entry.getValue();
				if (command.contains("assert")) {
					if (!question1.assertion(entry.getValue(), entry.getKey()))
						return false;
				}
			}
		}
	
		return true;
	}

}