import java.util.HashMap;
import java.util.Map;


public class Manager {

	public static Map<Integer, AbstractValue> initializeToBottom(int n){
		Map<Integer,AbstractValue> init=new HashMap<Integer,AbstractValue>();
		for(int i=0; i<n; i++) {
			init.put(i, new AbstractValue("BOTTOM"));
		}
		return init;
	}
	
	public static ControlGraph buildGraph() { // need to pass the input
		return null;
	}
	
}
