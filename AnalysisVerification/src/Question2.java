import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import ast.*;

public class Question2  { //extends Question {
	//@Override
	CPVisitor applyAbstractFunction(Map<String, Integer> inState, Map<String, Map<String, Integer>> inSums,  Command command) {
		CPVisitor v = new CPVisitor(inState,inSums);
		command.accept(v);
		return v;
	}
	

	private Integer joinPointWise(Integer value1, Integer value2) { // Integer works with .equals?
		boolean eitherIsTop = (value1.equals(CPVisitor.TOP) || value2.equals(CPVisitor.TOP));
		boolean bothBottom = (value1.equals(CPVisitor.BOTTOM) && value2.equals(CPVisitor.BOTTOM));
		boolean notSame = (!value1.equals(CPVisitor.TOP) && !value1.equals(CPVisitor.BOTTOM)
			&&!value2.equals(CPVisitor.TOP) && !value2.equals(CPVisitor.BOTTOM)&& !value2.equals(value1));
		
		if (eitherIsTop || notSame)
			return CPVisitor.TOP ;
		else if (bothBottom)
			return CPVisitor.BOTTOM;
		else if (!value1.equals(CPVisitor.BOTTOM))
			return value1;
		else
			return value2;
	}
	
	//@Override
	public Map<String, Integer> join(Map<String, Integer> state1, Map<String, Integer> state2){
		Map<String, Integer> output = new HashMap<>();
		// join pointwise
		for (String varName : state1.keySet()) {
			output.put(varName, joinPointWise(state1.get(varName), state2.get(varName)));
		}
		return output;
	}

	public Map<String, Map<String, Integer>> joinRelState(Map<String, Map<String, Integer>> relState1, Map<String, Map<String, Integer>> relState2) {
		Map<String, Map<String, Integer>> outRels = new HashMap<>();
		Set<String> variables = relState1.keySet();
		for (String var : variables) {
			outRels.put(var, new HashMap<>());
		}
		// join sums in each map, one by one
		for (String var1 : variables) {
			for (String var2 : variables) {
				if (!var1.equals(var2)) {
					Integer diff1 = relState1.get(var1).get(var2);
					Integer diff2 = relState2.get(var1).get(var2);
					Integer resultDiff = joinPointWise(diff1, diff2);
					outRels.get(var1).put(var2, resultDiff);
					outRels.get(var2).put(var1, resultDiff);
				}
			}
		}
		return outRels;
	}	

}