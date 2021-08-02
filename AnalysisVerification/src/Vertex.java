import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Vertex {

	String label;
	Map<Vertex,String> pointedBy;
	List<Vertex> pointTo;
	Map<String, String> state; //every vertex has a state
	
	
	public Vertex(String label, String[] varList) {
		pointedBy = new HashMap<Vertex, String>();
		pointTo = new ArrayList<Vertex>();
		
		this.label = label;
		this.state = Manager.initializeToBottom(varList);
	}
}
