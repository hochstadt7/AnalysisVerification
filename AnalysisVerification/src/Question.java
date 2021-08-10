import java.util.Map;
import ast.Command;
public abstract class Question {

	abstract Map<String, String> applyAbstractFunction(Map<String, String> variables, Command command);
	abstract Map<String, Map<String, String>> computeRelations(Map<String, String> state);
	abstract Map<String, String> join(Map<String, String> state1, Map<String, String> state2);
	abstract boolean assertion(String assertCommand,Vertex last);
	
}