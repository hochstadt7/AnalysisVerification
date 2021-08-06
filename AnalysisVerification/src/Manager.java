import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Manager {

	// states are initialized to hold BOTTOMS as abstract value
	public static Map<String, String> initializeState(String[] varList, String val){
		Map<String,String> init = new HashMap<>();
		
		for (int i = 0; i < varList.length; i++) {
			init.put(varList[i], val);
		}
		return init;
	}
	
	// build graph based on the input
	public static ControlGraph buildGraph(Scanner in, String []varList) {
		ControlGraph controlGraph = new ControlGraph();
		Map<String, Vertex> namedVertices = new HashMap<>();
		while(in.hasNextLine()) {
			String[] line = in.nextLine().split(" ");
			String source = line[0];
			String[] subarray = Arrays.copyOfRange(line, 1, line.length-1);
			String command = String.join(" ", subarray).trim();
			String target = line[line.length-1];
			Vertex src;
			Vertex dest;
			if (!(namedVertices.containsKey(source))) {
				src = new Vertex(source, varList);
				controlGraph.vertices.add(src);				
				namedVertices.put(source, src);
			}
			if (!(namedVertices.containsKey(target))) {
				dest = new Vertex(target, varList);
				controlGraph.vertices.add(dest);
				namedVertices.put(target, dest);
			}
			namedVertices.get(target).pointedBy.put(namedVertices.get(source), command); // target pointed by source
			namedVertices.get(source).pointTo.add(namedVertices.get(target)); //source points to target
		}
		controlGraph.names = namedVertices;
		return controlGraph;
	}
	
	public static boolean isNum(String possibleNum) {
		try { // constant assignment
			int isOk=Integer.parseInt(possibleNum);
			return true;
		}
		
		catch(NumberFormatException e){
			return false;
		}
	}
	
}
