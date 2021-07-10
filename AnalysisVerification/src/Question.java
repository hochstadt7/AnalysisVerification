import java.util.HashMap;
import java.util.Map;

public abstract class Question {

	
	
	abstract Map<Integer, AbstractValue> activateAbstractFunction(Map<Integer, AbstractValue> variables, String command);
	abstract Map<Integer, AbstractValue> union(Map<Integer, AbstractValue> value1, Map<Integer, AbstractValue> value2);
	abstract boolean assertion(String assertCommand,Map<String,Integer> variables,Vertex last);
	
}
