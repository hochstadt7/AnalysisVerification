import java.io.StringReader;
import java.util.*;

import ast.*;

public class Manager {

	public static Map<String, CartesianProduct> initializeCartesianState(String[] varList, CartesianProduct val){
		Map<String, CartesianProduct> varValues = new HashMap<>();
		for (String var : varList){
			varValues.put(var, val);
		}

		return varValues;
	}

	// states are initialized to hold BOTTOMS as abstract value
	public static Map<String, String> initializeParityState(String[] varList, String val){
		Map<String, String> varValues = new HashMap<>();
		for (String s : varList) {
			varValues.put(s, val);
		}
		return varValues;
	}

	public static Map<String, Integer> initializeCPState(String[] varList, Integer val) {
		Map<String, Integer> varValues = new HashMap<>();
		for (String s : varList) {
			varValues.put(s, val);
		}
		return varValues;
	}

	public static Map<String, Map<String, String>> initRelationalParity(String[] varList, String val) {
		Map<String, Map<String, String>> relations = new HashMap<>();
		for (String var : varList) {
			Map<String, String> varRelations = new HashMap<>();
			for (String otherVar : varList) {
				if (!var.equals(otherVar)) {
					varRelations.put(otherVar, val);
				}
			}
			relations.put(var, varRelations);
		}
		return relations;
	}
	
	// build graph based on the input
	public static ControlGraph buildGraph(Scanner in, String[] varList) {
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
		controlGraph.initControlGraph(varList);
		return controlGraph;
	}

	// Updates CP factoids and VE factoids from separate fixed points to get a more detailed view
	// given both analyses' results.
	public static void reduceUntilFixed(Map<String, Integer> inCP, Set<VariableEquality> inVE) {
		Map<String, Integer> newCP = new HashMap<>(inCP);
		Set<VariableEquality> newVE = new HashSet<>(inVE);

		while (true) {
			Map<String, Integer> currCP = new HashMap<>(newCP);
			Set<VariableEquality> currVE = new HashSet<>(newVE);

			// reduce right
			for (VariableEquality varEq : inVE) {
				String lv = varEq.getLv();
				String rv = varEq.getRv();
				Integer absValLv = currCP.get(lv);
				Integer absValRv = currCP.get(rv);

				if (!absValLv.equals(CPVisitor.TOP) && !absValLv.equals(CPVisitor.BOTTOM)) { // if lv = K => rv = K
					newCP.put(rv, absValLv);
				}
				if (!absValRv.equals(CPVisitor.TOP) && !absValRv.equals(CPVisitor.BOTTOM)) { // if rv = K => lv = K
					newCP.put(lv, absValRv);
				}
			}

			// reduce left
			for (String lv : currCP.keySet()) {
				for (String rv : currCP.keySet()) {
					if (!lv.equals(rv)) {
						Integer absValLv = currCP.get(lv);
						Integer absValRv = currCP.get(rv);
						if (absValLv.equals(absValRv) && !absValLv.equals(CPVisitor.TOP) &&
								!absValLv.equals(CPVisitor.BOTTOM)) { // both vals are the same numbers
							newVE.add(new VariableEquality(lv, rv));
						}
					}
				}
			}
			// fixed point for both analysis
			if (currVE.size() == newVE.size() && currVE.containsAll(newVE) &&
					currCP.equals(newCP)) {
				break;
			}
		}
		// need to somehow return both inVE and inCP for the assertionVisitor coming later
		inCP.clear(); inCP.putAll(newCP);
		inVE.clear(); inVE.addAll(newVE);
	}
}
