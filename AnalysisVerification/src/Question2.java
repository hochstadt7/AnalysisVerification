import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question2 extends Question {
	@Override
	Map<String, String> activateAbstractFunction(Map<String, String> variables, String command) {
		
		Map<String, String> output=new HashMap<> (variables); // make a copy
		
		 if (command.equals("skip"))
			return output; // state hasn't changed
		
		/* we need to discuss about assume*/
		else  if (command.contains("assume")) {
			String afterAssume=command.substring(7);
			String[] split=afterAssume.split(" ");
			 if (split[1].contains("!")) {
				 if ((variables.get(split[0]).equals("EVEN") && Integer.parseInt(split[2]) % 2 == 0)
						|| (variables.get(split[0]).equals("EVEN") && Integer.parseInt(split[2]) % 2 == 1)) {
					return output;
				}
				else {
					for(String a: output.keySet()) {
						output.put(a, "BOTTOM");
					}
					return output;
				}
			}
			else {
				 if ((variables.get(split[0]).equals("EVEN")&&Integer.parseInt(split[2])%2==1)
						||(variables.get(split[0]).equals("EVEN")&&Integer.parseInt(split[2])%2==0)) {
					return output;
				}
				else {
					for(String a: output.keySet()) {
						output.put(a, "BOTTOM");
					}
					return output;
				}
				
			}
			
		}
			
		else if (command.contains("assert")) {
			return output;
		}
		
		else {
			int equal=command.indexOf("=");
			String afterEqual=command.substring(equal+1);
			String beforeEqual=command.substring(0,equal);
			 if (afterEqual.contains("+")|| afterEqual.contains("-")) { // operator assignment
				String tmp= (variables.get(afterEqual).equals("ODD")? "EVEN": "ODD");
				output.put(beforeEqual, tmp);
			}
			else  if (afterEqual.contains("?")) {
				output.put(beforeEqual, variables.get(afterEqual));
			}
			
			else  if (Character.isDigit(afterEqual.charAt(0))) { //variable assignment
				
			}
			
			else { // constant assignment
				String tmp=(Integer.parseInt(String.valueOf(afterEqual.charAt(0)))%2==0? "EVEN": "ODD");
				output.put("beforeEqual", tmp);
			}
		}
		
		
		return output;
		
	}
	
	
	private String unionPointWise(String value1, String value2) {

		 if ((value1.equals("TOP")||value2.equals("TOP"))||(value1.equals("EVEN")&&value2.equals("ODD"))||(value1.equals("ODD")&&value2.equals("EVEN")))
			return "TOP";
		
		else  if (value1.equals("BOTTOM")&&value2.equals("BOTTOM"))
			return "BOTTOM";
		else  if (value1.equals("ODD"))
			return "ODD";
		else
			return "EVEN";
		
	}
	
	@Override
	public Map<String, String> union(Map<String, String> value1, Map<String, String> value2,String []varList){
		
		Map<String, String> output=new HashMap<String, String>();
		//union pointwise
		for(String str:varList) {
			output.put(str, unionPointWise(value1.get(str),value2.get(str)));
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
			    if (validate(str.substring(1,str.length()-1),last))
				   return true; // it is enough that one conjunction will be true
		}
		return false;
	}
	
	
	public boolean validate(String andCondition, Vertex last) {
		
		String[] arr=andCondition.split(" ");
		for(int i=0; i<arr.length/2; i++) {
			 if (!(arr[i].equals(last.state.get(arr[i+1]))))
				return false;
		}
		return true;
		
	}
	
	

}