import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import ast.*;

public class ParityAnalysis {
	ParityVisitor applyAbstractFunction(Map<String, String> inState, Map<String, Map<String, String>> inDiff,  Command command) {
		ParityVisitor v = new ParityVisitor(inState,inDiff);
		command.accept(v);
		return v;
	}

	private String joinPointWise(String value1, String value2) {
		boolean eitherIsTop = (value1.equals(ParityVisitor.TOP) || value2.equals(ParityVisitor.TOP));
		boolean bothBottom = (value1.equals(ParityVisitor.BOTTOM) && value2.equals(ParityVisitor.BOTTOM));
		boolean areOpposite = (value1.equals(ParityVisitor.EVEN) && value2.equals(ParityVisitor.ODD))
				|| (value1.equals(ParityVisitor.ODD) && value2.equals(ParityVisitor.EVEN));
		if (eitherIsTop || areOpposite)
			return ParityVisitor.TOP ;
		else if (bothBottom)
			return ParityVisitor.BOTTOM;
		else if (value1.equals(ParityVisitor.ODD) || value2.equals(ParityVisitor.ODD))
			return ParityVisitor.ODD;
		else
			return ParityVisitor.EVEN;
	}

	public Map<String, String> join(Map<String, String> state1, Map<String, String> state2){
		Map<String, String> output = new HashMap<>();
		// join pointwise
		for (String varName : state1.keySet()) {
			output.put(varName, joinPointWise(state1.get(varName), state2.get(varName)));
		}
		return output;
	}

	public Map<String, Map<String, String>> joinRelState(Map<String, Map<String, String>> relState1, Map<String, Map<String, String>> relState2) {
		Map<String, Map<String, String>> outRels = new HashMap<>();
		Set<String> variables = relState1.keySet();
		for (String var : variables) {
			outRels.put(var, new HashMap<>());
		}
		// join diffs in each map, one by one
		for (String var1 : variables) {
			for (String var2 : variables) {
				if (!var1.equals(var2)) {
					String diff1 = relState1.get(var1).get(var2);
					String diff2 = relState2.get(var1).get(var2);
					String resultDiff = joinPointWise(diff1, diff2);
					outRels.get(var1).put(var2, resultDiff);
					outRels.get(var2).put(var1, resultDiff);
				}
			}
		}
		return outRels;
	}

	
	
	

}