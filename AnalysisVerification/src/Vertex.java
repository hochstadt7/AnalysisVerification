import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Vertex {

	String label;
	Map<Vertex,Edge> pointedBy;
	List<Vertex> pointTo;
	Map<String, AbstractValue> state; //every vertex has a state
	
	
	public Vertex(int n,  String label, String []varList) {
		pointedBy=new HashMap<Vertex,Edge>();
		pointTo=new ArrayList<Vertex>();
		
		this.label=label;
		this.state=Manager.initializeToBottom(n,varList);
	}
}
