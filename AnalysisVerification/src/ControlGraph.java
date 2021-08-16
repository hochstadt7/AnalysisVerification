import ast.CPVisitor;
import ast.CartesianVisitor;
import ast.ParityVisitor;

import java.util.*;

public class ControlGraph {
	Vertex start;
	Map<String, Vertex> namedVertices;

	public void initControlGraph(String[] varList) {
		// initialization
		this.start.parityState = Manager.initializeParityState(varList, ParityVisitor.TOP);
		this.start.relationalParityState = Manager.initRelationalParity(varList, ParityVisitor.TOP);
		this.start.CPState = Manager.initializeCPState(varList, CPVisitor.TOP);
		this.start.VEState = new HashSet<>();

		Set<String> setVars = new HashSet<>(Arrays.asList(varList));
		this.start.cartesianState = Manager.initializeCartesianState(varList, CartesianVisitor.bottomProduct(setVars));

		for (Vertex v : this.namedVertices.values()) {
			if (v.parityState == null) {
				v.parityState = Manager.initializeParityState(varList, ParityVisitor.BOTTOM);
				v.relationalParityState = Manager.initRelationalParity(varList, ParityVisitor.BOTTOM);
			}
			if (v.CPState == null) {
				v.CPState = Manager.initializeCPState(varList, CPVisitor.BOTTOM);
				v.VEState = new HashSet<>();
				v.cartesianState = Manager.initializeCartesianState(varList, CartesianVisitor.bottomProduct(setVars));
			}
		}
	}
}
