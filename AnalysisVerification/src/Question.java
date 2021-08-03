import java.util.Map;

public abstract class Question {

	abstract Map<String, String> activateAbstractFunction(Map<String, String> variables, String command);
	abstract Map<String, String> join(Map<String, String> value1, Map<String, String> value2,String []varList);
	abstract boolean assertion(String assertCommand,Vertex last);
	
}