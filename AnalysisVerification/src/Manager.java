import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Manager {

	// states are initialized to hold BOTTOMS as abstract value
	public static Map<String, String> initializeToBottom(int n,String []varList){
		Map<String,String> init=new HashMap<String,String>();
		for(String str:varList) {
			init.put(str, "BOTTOM");
		}
		return init;
	}
	
	// build graph based on the input
	public static ControlGraph buildGraph(Scanner in ,int n,String []varList) {
		
		
		ControlGraph controlGraph=new ControlGraph();
		Set<String> labels=new HashSet<String>(); // to remember for which variables we have already created a vertex
		Map<String,Vertex> names=new HashMap<String,Vertex>(); //map between variables names, and vertices with those names as labels
		while(in.hasNextLine()) {
			String []row=in.nextLine().split(" ");
			String source=row[0];
			String[] subarray = Arrays.copyOfRange(row, 1, row.length-2);
			String command=String.join(" ", subarray).trim();
			String target=row[row.length-1];
			
			Vertex src;
			Vertex dest;
			if (!(labels.contains(source))) {
				labels.add(source);
				src=new Vertex(n,source,varList);
				controlGraph.vertices.add(src);				
				names.put(source, src);
			}
			if (!(labels.contains(target))) {
				labels.add(target);
				dest=new Vertex(n,target,varList);
				controlGraph.vertices.add(dest);
				names.put(target, dest);
			}
			names.get(target).pointedBy.put(names.get(source), command); // target pointed by source
			names.get(source).pointTo.add(names.get(target)); //source points to target
			
		}
		controlGraph.names=names;
		return controlGraph;
	}
	
}
