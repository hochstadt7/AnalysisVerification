import java.util.Map;
import ast.Command;
import ast.ParityVisitor;
public abstract class Question<P,T> {

	abstract P applyAbstractFunction(Map<String, T> variables, Map<String, Map<String, T>> inDiff, Command command);
	//abstract Map<String, Map<String, String>> computeRelations(Map<String, String> state);
	abstract Map<String, T> join(Map<String, T> state1, Map<String, T> state2);
	//abstract boolean assertion(String assertCommand,Vertex last);
	
}