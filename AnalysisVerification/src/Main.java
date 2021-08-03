import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;

public class Main {
	
	
	static Question1 question1;
	

	//expected input format: list of vars with one space separator, than each line has vertex- 2 spaces- command- 2 spaces- vertex
	public static void main(String[] args) throws FileNotFoundException {
		
		question1 = new Question1();
		//String input = args[0];
		
		Scanner in = new Scanner(new File("./AnalysisVerification/src/Input.txt")).useDelimiter(" ");
		String[] varList = in.nextLine().split(" "); // first line is the variables
		System.out.println(varList);
		ControlGraph controlGraph = Manager.buildGraph(in, varList);
		System.out.println(chaoticIteration(controlGraph, varList));
	}
	
	// based on the algorithm in lecture 7, page 108
	public static boolean chaoticIteration(ControlGraph controlGraph, String[] varList) {

		String popName = ""; // the label of the vertex we pop at the chaotic iteration
		
		// initialization
		controlGraph.vertices.get(0).state = Manager.initializeToBottom(varList,"TOP");
		int numOfVars=varList.length;
		for (int i = 1; i<numOfVars; i++) {
			controlGraph.vertices.get(i).state = Manager.initializeToBottom(varList,"BOTTOM");
		}

		Set<String> workList = new HashSet<>(controlGraph.names.keySet());
		
		while(!workList.isEmpty()) { // need to check edge case where there is no variable at the beginning?
			
			for (String name:controlGraph.names.keySet()) { // is there better way to remove item from a set, and get his value? I hate Sets in Java
				if (workList.contains(name)) { // supposed to happen at some iteration
					workList.remove(name);
					popName = name;
					break;
				}
			}
			int index = controlGraph.vertices.indexOf(controlGraph.names.get(popName)); // those two lines can be simplified, but basically we retrieve the state of the vertex with popLabel
			Map<String, String> newState = controlGraph.vertices.get(index).state;
			
			// the new state of our current vertex is the union of all vertices point to the vertex, after activation of the corresponding abstract function
			for (Entry<Vertex, String> entry : controlGraph.vertices.get(index).pointedBy.entrySet()) {
				newState = question1.union(newState, question1.activateAbstractFunction(entry.getKey().state, entry.getValue()), varList);
			}
			
			if (newState != controlGraph.vertices.get(index).state) {
				controlGraph.vertices.get(index).state = newState;
				for (Vertex v : controlGraph.vertices.get(index).pointTo) { // append all vertices pointed by our current vertex
					workList.add(v.label);
				}
			}
			
		}
		for (Vertex v:controlGraph.vertices) {
			for (Entry<Vertex, String> entry : v.pointedBy.entrySet()) {
				if (entry.getValue().contains("assert")) {
					if (!question1.assertion(entry.getValue(), entry.getKey()))
						return false;
				}
			}
		}
	
		return true;
	}

}