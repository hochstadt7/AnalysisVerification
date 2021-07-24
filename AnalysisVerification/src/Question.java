import java.util.HashMap;
import java.util.Map;

public abstract class Question {

	
	
	abstract Map<String, AbstractValue> activateAbstractFunction(Map<String, AbstractValue> variables, String command);
	abstract Map<String, AbstractValue> union(Map<String, AbstractValue> value1, Map<String, AbstractValue> value2,String []varList);
	abstract boolean assertion(String assertCommand,Vertex last);
	
}
