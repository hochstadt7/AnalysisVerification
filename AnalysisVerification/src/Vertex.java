import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ast.Command;

public class Vertex {
	String label;
	Map<Vertex, Command> pointedBy; // incoming neighbor and command associated with edge
	List<Vertex> pointsTo;
	Map<String, String> state; // 1 var state - maps variable to abstract value
	Map<String, Map<String, String>> relationalState;
	// 2 var state - map a selected relation (analysis dependent) between variables to an abs val
	
	public Vertex(String label) {
		pointedBy = new HashMap<>();
		pointsTo = new ArrayList<>();
		
		this.label = label;
		this.state = null;
		this.relationalState = null;
	}
}
