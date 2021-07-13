import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Manager {

	// states are initialized to hold BOTTOMS as abstract value
	public static Map<String, AbstractValue> initializeToBottom(int n,String []varList){
		Map<String,AbstractValue> init=new HashMap<String,AbstractValue>();
		for(String str:varList) {
			init.put(str, new AbstractValue("BOTTOM"));
		}
		return init;
	}
	
	// build graph based on the input
	public static ControlGraph buildGraph(Scanner in ,int n,String []varList) {
		
		
		ControlGraph controlGraph=new ControlGraph();
		Set<String> labels=new HashSet<String>(); // to remember for which variables we have already created a vertex
		Map<String,Vertex> names=new HashMap<String,Vertex>(); //map between variables names, and vertices with those names as labels
		while(in.hasNextLine()) {
			String source=in.next();
			String command=in.next();
			String target=in.next(); // now it passed a line??
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
			names.get(target).pointedBy.put(names.get(source), new Edge(command)); // target pointed by source
			names.get(source).pointTo.add(names.get(target)); //source points to target
			
		}
		controlGraph.names=names;
		return controlGraph;
	}
	
}
