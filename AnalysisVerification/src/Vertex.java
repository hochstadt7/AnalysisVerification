import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ast.Command;
import ast.VariableEquality;

public class Vertex {
	String label;
	Map<Vertex, Command> pointedBy; // incoming neighbor and command associated with edge
	List<Vertex> pointsTo;
	Map<String, String> parityState;
	Map<String, Map<String, String>> relationalParityState;
	Set<VariableEquality> VEState;
	Map<String, Integer> CPState;

	public Vertex(String label) {
		pointedBy = new HashMap<>();
		pointsTo = new ArrayList<>();
		
		this.label = label;
		this.parityState = null;
		this.relationalParityState = null;
		this.VEState = null;
		this.CPState = null;
	}
}
