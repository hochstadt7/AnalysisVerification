import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import ast.*;

public class CPAnalysis {
	CPVisitor applyAbstractFunction(Map<String, Integer> inState, Command command) {
		CPVisitor v = new CPVisitor(inState);
		command.accept(v);
		return v;
	}

	private Integer joinPointWise(Integer value1, Integer value2) { // Integer works with .equals?
		boolean eitherIsTop = (value1.equals(CPVisitor.TOP) || value2.equals(CPVisitor.TOP));
		boolean bothBottom = (value1.equals(CPVisitor.BOTTOM) && value2.equals(CPVisitor.BOTTOM));
		boolean notSameActualVal = (!value1.equals(CPVisitor.TOP) && !value1.equals(CPVisitor.BOTTOM)
			&& !value2.equals(CPVisitor.TOP) && !value2.equals(CPVisitor.BOTTOM) && !value2.equals(value1));
		
		if (eitherIsTop || notSameActualVal)
			return CPVisitor.TOP ;
		else if (bothBottom)
			return CPVisitor.BOTTOM;
		else if (!value1.equals(CPVisitor.BOTTOM))
			return value1;
		else
			return value2;
	}
	
	public Map<String, Integer> join(Map<String, Integer> state1, Map<String, Integer> state2){
		Map<String, Integer> output = new HashMap<>();
		// join pointwise
		for (String varName : state1.keySet()) {
			output.put(varName, joinPointWise(state1.get(varName), state2.get(varName)));
		}
		return output;
	}
}