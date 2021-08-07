import java.io.FileReader;
import java.io.StringReader;
import java.util.*;
import ast.Command;

public class Manager {

	// states are initialized to hold BOTTOMS as abstract value
	public static Map<String, String> initializeState(String[] varList, String val){
		Map<String, String> varValues = new HashMap<>();
		for (String s : varList) {
			varValues.put(s, val);
		}
		return varValues;
	}
	
	// build graph based on the input
	public static ControlGraph buildGraph(Scanner in) {
		ControlGraph controlGraph = new ControlGraph();
		Map<String, Vertex> namedVertices = new HashMap<>();
		List<Vertex> orderedVertices = new ArrayList<>();
		while (in.hasNextLine()) {
			String[] line = in.nextLine().split(" ");
			String source = line[0];
			String[] subarray = Arrays.copyOfRange(line, 1, line.length-1);
			String commandText = String.join(" ", subarray).trim();
			String target = line[line.length-1];
			Vertex src;
			Vertex dest;
			if (!(namedVertices.containsKey(source))) {
				src = new Vertex(source);
				orderedVertices.add(src);
				namedVertices.put(source, src);
			}
			if (!(namedVertices.containsKey(target))) {
				dest = new Vertex(target);
				orderedVertices.add(dest);
				namedVertices.put(target, dest);
			}
			Parser p = new Parser(new Lexer(new StringReader(commandText)));
			Command command;
			try {
				command = (Command) (p.parse().value);
				namedVertices.get(target).pointedBy.put(namedVertices.get(source), command); // target pointed by source
				namedVertices.get(source).pointsTo.add(namedVertices.get(target)); //source points to target
			} catch (Exception e) {
				System.out.println("Parser error: " + e);
				e.printStackTrace();
				System.exit(1);
			}
		}
		controlGraph.namedVertices = namedVertices;
		controlGraph.start = orderedVertices.get(0);
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
