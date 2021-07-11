import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question1 extends Question {
	
	@Override
	Map<Integer, AbstractValue> activateAbstractFunction(Map<Integer, AbstractValue> variables, String command) {
		
		HashMap<Integer, AbstractValue> output=new HashMap<Integer,AbstractValue> (variables); // make a copy
		
		switch (command) {
		case "Skip":
			return output;
		
		case "i=j": // need to parse the command in order to extract i and j
			output.put(0,variables.get(1));
		}
		return output;
		
	}
	
	private AbstractValue unionPointWise(AbstractValue value1, AbstractValue value2) {

		if((value1.val.equals("TOP")||value2.val.equals("TOP"))||(value1.val.equals("EVEN")&&value2.val.equals("ODD"))||(value1.val.equals("ODD")&&value2.val.equals("EVEN")))
			return new AbstractValue("TOP");
		
		else if(value1.val.equals("BOTTOM")&&value2.val.equals("BOTTOM"))
			return new AbstractValue("BOTTOM");
		else if(value1.val.equals("ODD"))
			return new AbstractValue("ODD");
		else
			return new AbstractValue("EVEN");
		
	}
	
	@Override
	public Map<Integer, AbstractValue> union(Map<Integer, AbstractValue> value1, Map<Integer, AbstractValue> value2){
		
		Map<Integer, AbstractValue> output=new HashMap<Integer, AbstractValue>();
		//union pointwise
		for(int i=0; i<value1.size(); i++) {
			output.put(i, unionPointWise(value1.get(i),value2.get(i)));
		}
		return output;
	}

	@Override
	
	boolean assertion(String assertCommand,Map<String,Integer> variables,Vertex last) { // pass this function to be implemented at question.java?
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("\\((.*?)\\)");
		Matcher regexMatcher = regex.matcher(assertCommand);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
		   matchList.add(regexMatcher.group(1));//Fetching Group from String
		}
		for(String str:matchList) {
			   if(validate(str,variables,last))
				   return true;
			}
		return false;
	}
	
	
	//controlGraph.vertices[n-1].state as input?
	public boolean validate(String andCondition, Map<String,Integer> variables, Vertex last) {
		Pattern regex = Pattern.compile("EVEN");
		Matcher regexMatcher = regex.matcher(andCondition);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
		   String var=andCondition.substring(regexMatcher.start(),regexMatcher.end());
		   if(!( last.state.get(variables.get(var.charAt(5))).equals("EVEN")))
			   return false;
		}
		
		regex = Pattern.compile("ODD");
		regexMatcher = regex.matcher(andCondition);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
		   String var=andCondition.substring(regexMatcher.start(),regexMatcher.end());
		   if(!(last.state.get(variables.get(var.charAt(4))).equals("ODD")))
			   return false;
		}
		return true;
	}
	
	

}
