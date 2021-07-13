import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question1 extends Question {
	
	
	
	@Override
	Map<String, AbstractValue> activateAbstractFunction(Map<String, AbstractValue> variables, String command) {
		
		HashMap<String, AbstractValue> output=new HashMap<String,AbstractValue> (variables); // make a copy
		
		
		if(command.equals("skip"))
			return output; // state hasn't changed
		
		else if(command.contains("assume")) {
			
		}
			
		else if (command.contains("assert")) {
			
		}
		
		else {
			int equal=command.indexOf("=");
			String afterEqual=command.substring(equal+1);
			String beforeEqual=command.substring(0,equal);
			if(afterEqual.contains("+")|| afterEqual.contains("-")) { // operator assignment
				AbstractValue tmp= (variables.get(afterEqual).val=="ODD"? new AbstractValue("EVEN"): new AbstractValue("ODD"));
				output.put(beforeEqual, tmp);
			}
			else if(afterEqual.contains("?")) {
				output.put(beforeEqual, variables.get(afterEqual));
			}
			
			else if(Character.isDigit(afterEqual.charAt(0))) { //variable assignment
				
			}
			
			else { // constant assignment
				AbstractValue tmp=(Integer.parseInt(String.valueOf(afterEqual.charAt(0)))%2==0? new AbstractValue("EVEN"): new AbstractValue("ODD"));
				output.put("beforeEqual", tmp);
			}
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
	public Map<String, AbstractValue> union(Map<String, AbstractValue> value1, Map<String, AbstractValue> value2,String []varList){
		
		Map<String, AbstractValue> output=new HashMap<String, AbstractValue>();
		//union pointwise
		for(String str:varList) {
			output.put(str, unionPointWise(value1.get(str),value2.get(str)));
		}
		return output;
	}

	@Override
	
	boolean assertion(String assertCommand,Map<String,Integer> variables,Vertex last) { //move this function to be implemented at question.java?
		List<String> matchList = new ArrayList<String>();
		Pattern regex = Pattern.compile("\\((.*?)\\)"); //find all the parenthesis
		Matcher regexMatcher = regex.matcher(assertCommand);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
		   matchList.add(regexMatcher.group(1));//Fetching Group from String
		}
		for(String str:matchList) {
			   if(validate(str,variables,last))
				   return true; // it is enough that one conjunction will be true
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
			   return false; // all the disjunctions have to be true
		}
		
		regex = Pattern.compile("ODD");
		regexMatcher = regex.matcher(andCondition);

		while (regexMatcher.find()) {//Finds Matching Pattern in String
		   String var=andCondition.substring(regexMatcher.start(),regexMatcher.end());
		   if(!(last.state.get(variables.get(var.charAt(4))).equals("ODD"))) //why charAt(4)? because the format of the input, just look at the examples
			   return false;
		}
		return true;
	}
	
	

}
