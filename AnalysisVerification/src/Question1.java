import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question1 extends Question {
	
	
	
	@Override
	Map<String, String> activateAbstractFunction(Map<String, String> variables, String command) {
		
		Map<String, String> output=new HashMap<> (variables); // make a copy
		
		
		if(command.equals("skip"))
			return output; // state hasn't changed
		
		/* we need to discuss about assume a bit*/
		else if(command.startsWith("assume")) {
			String afterAssume=command.substring(7,command.length()-1); // get rid of "assume(" and ")"
			String[] split=afterAssume.split(" "); // split[0]=left split[1]=action split[2]=right
			if(split[1].contains("!")) { // inequality
				
				// condition satisfied, nothing changes
				boolean checkNum=Manager.isNum(split[2]);
				if(checkNum) {
					if((variables.get(split[0]).equals("EVEN")&&Integer.parseInt(split[2])%2==1) // need to check split[2] is number or var
							||(variables.get(split[0]).equals("ODD")&&Integer.parseInt(split[2])%2==0)) {
						return output;
					}
				}
				
				else if((variables.get(split[0]).equals("EVEN")&&variables.get(split[2]).equals("ODD")) // need to check split[2] is "TOP" or "BOTTOM"?
						||(variables.get(split[0]).equals("ODD")&&variables.get(split[2]).equals("EVEN"))) {
					return output;
				}
				
				else if(variables.get(split[0]).equals("TOP")) { // I am not sure what to do
					for(String a: output.keySet()) {
						output.put(a, "TOP");
					}
					return output;
				}
				
				
				else { //  this includes the case where the condition is not satisfied (or variables.get(split[0]) is BOTTOM(?))
					for(String a: output.keySet()) {
						output.put(a, "BOTTOM");
					}
					return output;
				}
			}
			else if(split[0].contains("=")){ // equality- we send to BOTTOM anyway? or TOP should send all to TOP? does it matter?
				for(String a: output.keySet()) {
					output.put(a, "BOTTOM");
				}
				return output;
				
			}
			
		}
			
		else if (command.startsWith("assert")) {
			return output; // no change, we will check all asserts at the end of chaotic iteration
		}
		
		else { // assignment
			int equal=command.indexOf("=");
			String afterEqual=command.substring(equal+2); // we stepped over the space
			afterEqual=afterEqual.substring(afterEqual.length()-1); // got rid of ")"
			String beforeEqual=command.substring(0,equal-1); // got rid of "(" and the space
			String[] splitAfter=afterEqual.split(" ");
			if(afterEqual.contains("+")|| afterEqual.contains("-")) { // operator assignment
				String tmp=variables.get(splitAfter[0]);
				
				if(tmp.equals("TOP")||tmp.equals("BOTTOM")) { // BOTTOM/TOP+1=BOTTOM/TOP
					output.put(beforeEqual, tmp);
				}
				
				else {
					tmp= (tmp.equals("ODD")? "EVEN": "ODD");
					output.put(beforeEqual, tmp);
				}
				
			}
			
			else if(afterEqual.contains("?")) {
				output.put(beforeEqual, "TOP"); // by definition
			}
			
			else {
				String tmp="";
				if (Manager.isNum(splitAfter[0])) {
					int isNum=Integer.parseInt(splitAfter[0]);
					tmp=(isNum%2==0?"EVEN":"ODD");
				}
				
				
				else{ // variable assignment
					tmp=variables.get(splitAfter[0]);
				}
				
				output.put(beforeEqual, tmp);   
			}
			
		}
		
		
		return output;
		
	}
	
	
	private String joinPointWise(String value1, String value2) {

		if((value1.equals("TOP")||value2.equals("TOP"))||(value1.equals("EVEN")&&value2.equals("ODD"))||(value1.equals("ODD")&&value2.equals("EVEN")))
			return "TOP";
		
		else if(value1.equals("BOTTOM")&&value2.equals("BOTTOM"))
			return "BOTTOM";
		else if(value1.equals("ODD"))
			return "ODD";
		else
			return "EVEN";
		
	}
	
	@Override
	public Map<String, String> join(Map<String, String> value1, Map<String, String> value2,String []varList){
		
		Map<String, String> output=new HashMap<String, String>();
		// join pointwise
		for(String str:varList) {
			output.put(str, joinPointWise(value1.get(str),value2.get(str)));
		}
		return output;
	}

	@Override
	
	boolean assertion(String assertCommand,Vertex last) {
		List<String> matchList = new ArrayList<String>();
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