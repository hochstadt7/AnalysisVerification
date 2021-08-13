import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	
	
	public void reduceUntilFixed(Map<String, Integer> inState, Set<VariableEquality> ve) {
		
		Map<String, Integer> newState = new HashMap<>(inState);
		Set<VariableEquality> newve = new HashSet<>(ve);
		
		while (true) {
			
			Map<String, Integer> currState = new HashMap<>(newState); 
			Set<VariableEquality> currve = new HashSet<>(newve);
			
			// reduce right
			for (VariableEquality varEq : ve) {
				String lv = varEq.getLv();
				String rv = varEq.getRv();
				Integer absValLv = currState.get(lv);
				Integer absValRv = currState.get(rv);
				
				if (!absValLv.equals(CPVisitor.TOP) && !absValLv.equals(CPVisitor.BOTTOM)) {
					newState.put(rv, absValLv);
				}
				if (!absValRv.equals(CPVisitor.TOP) && !absValRv.equals(CPVisitor.BOTTOM)) {
					newState.put(lv, absValRv);
				}
			}
			
			// reduce left
			for (String lv : currState.keySet()) {
				for (String rv : currState.keySet()) {
					if (!lv.equals(rv)) {
						Integer absValLv = currState.get(lv);
						Integer absValRv = currState.get(rv);
						if (absValLv.equals(absValRv) && !absValLv.equals(CPVisitor.TOP) &&
								!absValLv.equals(CPVisitor.BOTTOM)) { // both vals are the same numbers
							newve.add(new VariableEquality(lv,rv));
						}
					}
					
				}
			}
			// fixed point for both analysis
			if (currve.size() == newve.size() && currve.containsAll(newve) &&
					currState.equals(newState)) {
				break;
			}
		}
		
		// need to somehow return both newState and newve for the assertionVisitor coming later
		inState.clear(); inState.putAll(newState);
		ve.clear(); ve.addAll(newve);
		
	}
}