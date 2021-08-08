import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ast.*;

public class Question1 extends Question {
	@Override
	Map<String, String> applyAbstractFunction(Map<String, String> inState, Command command) {
		ParityVisitor v = new ParityVisitor(inState);
		command.accept(v);
		return v.getNewState();
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

	@Override
	
	boolean assertion(String assertCommand,Vertex last) {
		List<String> matchList = new ArrayList<>();
		Pattern regex = Pattern.compile("\\((.*?)\\)"); //find all the parenthesis
		Matcher regexMatcher = regex.matcher(assertCommand);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
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