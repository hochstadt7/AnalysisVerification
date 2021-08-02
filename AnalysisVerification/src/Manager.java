import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Manager {

	// states are initialized to hold BOTTOMS as abstract value
	public static Map<String, String> initializeToBottom(String[] varList){
		Map<String,String> init = new HashMap<>();
		init.put(varList[0], "TOP"); // only the first vertex is initialized to TOP
		for (int i = 1; i < varList.length; i++) {
			init.put(varList[i], "BOTTOM");
		}
		return init;
	}
	
	// build graph based on the input
	public static ControlGraph buildGraph(Scanner in, String []varList) {
		ControlGraph controlGraph = new ControlGraph();
		Set<String> labels = new HashSet<>(); // to remember for which variables we have already created a vertex
		Map<String, Vertex> names = new HashMap<>(); //map between variables names, and vertices with those names as labels
		while(in.hasNextLine()) {
			String[] line = in.nextLine().split(" ");
			String source = line[0];
			String[] subarray = Arrays.copyOfRange(line, 1, line.length-2);
			String command = String.join(" ", subarray).trim();
			String target = line[line.length-1];
			Vertex src;
			Vertex dest;
			if (!(labels.contains(source))) {
				labels.add(source);
				src = new Vertex(source, varList);
				controlGraph.vertices.add(src);				
				names.put(source, src);
			}
			if (!(labels.contains(target))) {
				labels.add(target);
				dest = new Vertex(target, varList);
				controlGraph.vertices.add(dest);
				names.put(target, dest);
			}
			names.get(target).pointedBy.put(names.get(source), command); // target pointed by source
			names.get(source).pointTo.add(names.get(target)); //source points to target
		}
		controlGraph.names = names;
		return controlGraph;
	}
	
}
