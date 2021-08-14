import ast.CPVisitor;
import ast.ParityVisitor;

import java.util.HashSet;
import java.util.Map;

public class ControlGraph {
	Vertex start;
	Map<String, Vertex> namedVertices;

	public void initControlGraph(String[] varList) {
		// initialization
		this.start.parityState = Manager.initializeParityState(varList, ParityVisitor.TOP);
		this.start.relationalParityState = Manager.initRelationalParity(varList, ParityVisitor.TOP);
		this.start.CPState = Manager.initializeCPState(varList, CPVisitor.TOP);
		this.start.VEState = new HashSet<>();
		for (Vertex v : this.namedVertices.values()) {
			if (v.parityState == null) {
				v.parityState = Manager.initializeParityState(varList, ParityVisitor.BOTTOM);
				v.relationalParityState = Manager.initRelationalParity(varList, ParityVisitor.BOTTOM);
			}
			if (v.CPState == null) {
				this.start.CPState = Manager.initializeCPState(varList, CPVisitor.BOTTOM);
				this.start.VEState = new HashSet<>();
			}
		}
	}
}
