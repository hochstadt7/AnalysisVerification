import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import ast.*;

public class Question1 extends Question {
	@Override
	Map<String, String> applyAbstractFunction(Map<String, String> inState, Command command) {
		ParityVisitor v = new ParityVisitor(inState);
		command.accept(v);
		return v.getNewState();
	}

	@Override
	Map<String, Map<String, String>> computeRelations(Map<String, String> state) {
		Map<String, Map<String, String>> outRels = new HashMap<>();
		for (String var : state.keySet()) {
			outRels.put(var, new HashMap<>());
		}
		for (String var1 : state.keySet()) {
			for (String var2 : state.keySet()) {
				if (!var1.equals(var2)) {
					String value1 = state.get(var1);
					String value2 = state.get(var2);
					// now we can make conclusions about the diffs
					boolean eitherIsBottom = (value1.equals(ParityVisitor.BOTTOM) || value2.equals(ParityVisitor.BOTTOM));
					boolean eitherIsTop = (value1.equals(ParityVisitor.TOP) || value2.equals(ParityVisitor.TOP));
					if (eitherIsBottom) {
						outRels.get(var1).put(var2, ParityVisitor.BOTTOM);
						outRels.get(var2).put(var1, ParityVisitor.BOTTOM);
					} else if (eitherIsTop) {
						outRels.get(var1).put(var2, ParityVisitor.TOP);
						outRels.get(var2).put(var1, ParityVisitor.TOP);
					} else {
						if (value1.equals(value2)) {
							outRels.get(var1).put(var2, ParityVisitor.EVEN);
							outRels.get(var2).put(var1, ParityVisitor.EVEN);
						}
						else {
							outRels.get(var1).put(var2, ParityVisitor.ODD);
							outRels.get(var2).put(var1, ParityVisitor.ODD);
						}
					}
				}
			}
		}
		return outRels;
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
	
	@Override
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

	@Override
	boolean assertion(String assertCommand, Vertex last) {
		List<String> matchList = new ArrayList<>();
		Pattern regex = Pattern.compile("\\((.*?)\\)"); //find all the parenthesis
		Matcher regexMatcher = regex.matcher(assertCommand);

		while (regexMatcher.find()) { //Finds Matching Pattern in String
		   matchList.add(regexMatcher.group(1));//Fetching Group from String
		}
		for(String str:matchList) {
			   if(validate(str.substring(1,str.length()-1),last)) // got rid of parenthesis
				   return true; // it is enough that one conjunction will be true
		}
		return false;
	}
	
	// true only if all conditions are satisfied
	public boolean validate(String andCondition, Vertex last) {
		
		String[] arr=andCondition.split(" ");
		for(int i=0; i<arr.length/2; i++) {
			if(!(arr[i].equals(last.state.get(arr[i+1]))))
				return false;
		}
		return true;
		
	}
	
	

}