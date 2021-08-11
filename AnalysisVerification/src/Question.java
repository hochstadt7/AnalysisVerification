import java.util.Map;
import ast.Command;
import ast.ParityVisitor;
public abstract class Question {

	abstract ParityVisitor applyAbstractFunction(Map<String, String> variables,Map<String, Map<String, String>> inDiff, Command command);
	abstract Map<String, Map<String, String>> computeRelations(Map<String, String> state);
	abstract Map<String, String> join(Map<String, String> state1, Map<String, String> state2);
	abstract boolean assertion(String assertCommand,Vertex last);
	
}