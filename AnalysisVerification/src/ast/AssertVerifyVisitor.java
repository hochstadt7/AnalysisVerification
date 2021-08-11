package ast;

import java.util.*;

public class AssertVerifyVisitor {
    Map<String, String> currState;
    Map<String, Map<String, String>> relationalState;
    Map<String, Map<String, Integer>> countSameParity;
    Map<String, Map<String, Integer>> countDiffParity;
    
    private Map<String, Map<String, Integer>> initCombination(){
    	Map<String, Map<String, Integer>> bottoms = new HashMap<>();
        for (String var : currState.keySet()) {
        	Map<String,Integer> internal=new HashMap<>();
        	for(String var2:currState.keySet()) {
        		if(!var.equals(var2)) {
        			internal.put(var2, 0);
        		}
        	}
            bottoms.put(var, internal);
        }
        return bottoms;
    }

    public AssertVerifyVisitor(Map<String, String> currState, Map<String, Map<String, String>> relationalState) {
        this.currState = currState;
        this.relationalState = relationalState;
        this.countSameParity = initCombination();
        this.countDiffParity = initCombination();
    }
    
    private void updateNewDiff(Map<String, Map<String, Integer>> countRelation, String var1, String var2) {
    	int count = countRelation.get(var1).get(var2);
    	countRelation.get(var1).put(var2 ,count + 1);
    	countRelation.get(var2).put(var1 ,count + 1); // same update- symmetric
    }
    
    private void checkRelationalParity(List<Predicate> andClause) {
    	
    	int countOddPred = 0;
    	String firstVal = andClause.get(0).getId();
    	String secondVal = andClause.get(1).getId();
    	
    	
    	for (Predicate pred : andClause) {
        	if (pred instanceof OddPred) {
        		countOddPred++;
        	}
    	}
    	
        if (countOddPred%2 == 0) { // same parity
        		
        	// update counter
        	updateNewDiff(countSameParity, firstVal, secondVal);
        		
        } else if (countOddPred%2 == 1) { // not same parity
        		
        	updateNewDiff(countDiffParity, firstVal, secondVal);
        		
        }
        
    }
    

    public boolean visit(AssertCmd assertCmd) {
        List<List<Predicate>> dnf = assertCmd.getDNF();
        // for dnf to hold, we need one and clause that holds
        boolean anyTrue = false;
        for (List<Predicate> andClause : dnf) {
            boolean anyFalse = false;
            int numOfPredicates = 0;
            for (Predicate pred : andClause) {
            	numOfPredicates++;
                if (!pred.acceptVerifier(this)) {
                    anyFalse = true;
                    
                }
            }
            if (numOfPredicates == 2) {
            	checkRelationalParity(andClause);
            }
            
            if (!anyFalse) { // and clause holds
                anyTrue = true;
                break;
            }
        }
        for (String var:currState.keySet()){
        	for (String var2:currState.keySet()){
        		if (!var.equals(var2)) {
        			if (countSameParity.get(var).get(var2) == 2) { // (EVEN var EVEN var2) (ODD var ODD var2)
        				if (relationalState.get(var).get(var2).equals("EVEN"))
        					return true;
        			}
        			if (countDiffParity.get(var).get(var2) == 2) { // (EVEN var ODD var2) (ODD var EVEN var2)
        				if (relationalState.get(var).get(var2).equals("ODD"))
        					return true;
        			}
        		}
        		
        	}
        }
        
        return anyTrue;
    }

    public boolean visit(EvenPred evenPred) {
        String parity = currState.get(evenPred.getId());
        return parity.equals(ParityVisitor.EVEN);
    }

    public boolean visit(OddPred oddPred) {
        String parity = currState.get(oddPred.getId());
        return parity.equals(ParityVisitor.ODD);
    }
}
