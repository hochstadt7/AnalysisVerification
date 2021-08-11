package ast;

import java.util.*;

public class AssertVerifyVisitor {
    Map<String, String> currState;
    Map<String, Map<String, String>> relationalState;

    public AssertVerifyVisitor(Map<String, String> currState, Map<String, Map<String, String>> relationalState) {
        this.currState = currState;
        this.relationalState = relationalState;
    }
    
    private boolean checkRelationalParity(List<Predicate> andClause) {
    	
    	int countOddPred = 0;
    	
    	
    	for (Predicate pred : andClause) {
        	if(pred instanceof OddPred) {
        		countOddPred++;
        	}
    	}
        	if(countOddPred%2==0) { // same parity
        		if(relationalState.get(andClause.get(0).getId()).get(andClause.get(1).getId()).equals("EVEN")) {
        			return true;
        		}
        		
        	}
        	
        	if(countOddPred%2==1) { // not same parity
        		if(relationalState.get(andClause.get(0).getId()).get(andClause.get(1).getId()).equals("ODD")) {
        			return true;
        		}
        		
        	}
			return false;
        
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
            if(numOfPredicates==2) {
            	if(checkRelationalParity(andClause)) {anyTrue=true; break;}
            }
            
            if (!anyFalse) { // and clause holds
                anyTrue = true;
                break;
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
