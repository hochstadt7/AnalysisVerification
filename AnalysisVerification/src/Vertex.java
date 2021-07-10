import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Vertex {

	Map<Vertex,Edge> pointedBy;
	List<Vertex> pointTo;
	Map<Integer, AbstractValue> state;
	int numberOfVertex;
	
	public Vertex(int n) {
		pointedBy=new HashMap<Vertex,Edge>();
		pointTo=new ArrayList<Vertex>();
		state=Manager.initializeToBottom(n); // need to initialize to bottom value for each integer
	}
}
