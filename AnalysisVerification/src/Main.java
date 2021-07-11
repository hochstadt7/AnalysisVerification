import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Main {
	
	
	static Question1 question1;
	

	public static void main(String[] args) {
		question1=new Question1();

	}
	
	public boolean chaoticIteration(int numOfVars) {
		
		Map<String,Integer> variables=null; // need to set it according to the input
		Set<Integer> workList=new HashSet<Integer>();
		ControlGraph controlGraph=Manager.buildGraph();
		int popIndex;
		
		for(int i=0; i<numOfVars; i++) {
			controlGraph.vertices[i].state=Manager.initializeToBottom(numOfVars);
		}
		
		for(int i=0; i<numOfVars; i++) {
			workList.add(i);
		}
		
		while(!workList.isEmpty()) {
			
			for(popIndex=0; popIndex<numOfVars; popIndex++) { // is there better way to remove item from a setand get his value?
				if(workList.contains(popIndex)) {
					workList.remove(popIndex);
					break;
				}
			}
			
			Map<Integer, AbstractValue> newState=controlGraph.vertices[popIndex].state;
			for (Entry<Vertex, Edge> entry : controlGraph.vertices[popIndex].pointedBy.entrySet()) {
				newState=question1.union(newState, question1.activateAbstractFunction(entry.getKey().state, entry.getValue().command));
			}
			if(newState!=controlGraph.vertices[popIndex].state) {
				controlGraph.vertices[popIndex].state=newState;
				for (Vertex v : controlGraph.vertices[popIndex].pointTo) {
					workList.add(v.numberOfVertex);
				}
			}
			
		}
		
		return question1.assertion("insert assertion command",variables,controlGraph.vertices[numOfVars-1]);
		
	}

}
