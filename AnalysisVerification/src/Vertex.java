import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ast.Command;


public class Vertex {
	String label;
	Map<Vertex, Command> pointedBy; // incoming neighbor and command associated with edge
	List<Vertex> pointsTo;
	Map<String, String> state; //every vertex has a state
	
	public Vertex(String label) {
		pointedBy = new HashMap<>();
		pointsTo = new ArrayList<>();
		
		this.label = label;
		this.state = null;
	}
}
