package ast;

import java.util.HashMap;
import java.util.Map;

public class ParityVisitor implements Visitor {
    public static final String ODD = "ODD";
    public static final String EVEN = "EVEN";
    public static final String TOP = "TOP";
    public static final String BOTTOM = "BOTTOM";

    private final Map<String, String> inState;
    private final Map<String, Map<String, String>> inDiff;
    Map<String, String> newState;
    Map<String, Map<String, String>> newDiff;
    private final Map<String, String> allBottoms;
    private final Map<String, Map<String, String>> allBottomsDiff;

    private Map<String, String> produceAllBottoms() {
        Map<String, String> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            bottoms.put(var, BOTTOM);
        }
        return bottoms;
    }
    
    private Map<String, Map<String, String>> produceAllBottomsDiff() {
        Map<String, Map<String, String>> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
        	Map<String,String> internal=new HashMap<>();
        	for(String var2:inState.keySet()) {
        		if(!var.equals(var2)) {
        			internal.put(var2, BOTTOM);
        		}
        	}
            bottoms.put(var, internal);
        }
        return bottoms;
    }

    public ParityVisitor(Map<String, String> inState, Map<String, Map<String, String>> inDiff) {
        this.inState = inState;
        this.inDiff=inDiff;
        this.newState = new HashMap<>(inState);
        this.newDiff=new HashMap<>(inDiff);
        
        this.allBottoms = produceAllBottoms();
        this.allBottomsDiff=produceAllBottomsDiff();
    }

    public Map<String, String> getNewState() {
        return newState;
    }
    
    public Map<String, Map<String, String>> getNewDiff() {
        return newDiff;
    }

    @Override
    public void visit(AssertCmd assertCmd) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(AssumeCmd assumeCmd) {
        assumeCmd.getExpr().accept(this);
    }

    @Override
    public void visit(IntAssignCmd intAssignCmd) {
    	
    	Map<String,String> internal=inDiff.get(intAssignCmd.getLv());
    	
        if (intAssignCmd.isAnyNum()) {
            newState.put(intAssignCmd.getLv(), TOP);
            
            // all relations are now unknown
            for(String s:internal.keySet()) {
            	//anything involves BOTTOM is BOTTOM
            	newDiff.get(intAssignCmd.getLv()).put(s, inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
            	newDiff.get(s).put(intAssignCmd.getLv(),inState.get(intAssignCmd.getLv()).equals(BOTTOM)?BOTTOM:TOP);
            }   
            
        } else {
            String newAbsVal = intAssignCmd.getVal() % 2 == 0 ? EVEN : ODD;
            newState.put(intAssignCmd.getLv(), newAbsVal);
            
            
            for (String s:internal.keySet()) {
            	
            	String oldAbsVal=inState.get(s);
            	
            	if (oldAbsVal.equals("ODD")) {
            		if (newAbsVal.equals("ODD")) {
            			//ODD - ODD = EVEN
            			newDiff.get(intAssignCmd.getLv()).put(s, EVEN);
            			newDiff.get(s).put(intAssignCmd.getLv(), EVEN);
            		}
            		else if (oldAbsVal.equals("EVEN")) {
            			// EVEN - ODD = ODD
            			newDiff.get(intAssignCmd.getLv()).put(s, ODD);
            			newDiff.get(s).put(intAssignCmd.getLv(), ODD);
            		}
            		else {
            			// everything involves BOTTOM is BOTTOM
            			newDiff.get(intAssignCmd.getLv()).put(s, inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
            			newDiff.get(s).put(intAssignCmd.getLv(), inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
            		}
            	} else if (oldAbsVal.equals("EVEN")) {
            		if(newAbsVal.equals("ODD")) {
            			//EVEN - ODD = ODD
            			newDiff.get(intAssignCmd.getLv()).put(s, ODD);
            			newDiff.get(s).put(intAssignCmd.getLv(), ODD);
            		}
            		else if (oldAbsVal.equals("EVEN")) {
            			//EVEN - EVEN = EVEN
            			newDiff.get(intAssignCmd.getLv()).put(s, EVEN);
            			newDiff.get(s).put(intAssignCmd.getLv(), EVEN);
            			
            		}
            		else {
        				// everything involves BOTTOM is BOTTOM
            			newDiff.get(intAssignCmd.getLv()).put(s, inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
            			newDiff.get(s).put(intAssignCmd.getLv(), inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
        			}
            	}
            }
        }
        
        
    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) {
    	Map<String,String> internal=inDiff.get(varAssignCmd.getLv());
    
         if (varAssignCmd.getType() == VarAssignCmd.AssignType.SIMPLE) {
             newState.put(varAssignCmd.getLv(), inState.get(varAssignCmd.getRv()));
             
             for (String s:internal.keySet()) {
            	 if (s.equals(varAssignCmd.getRv())) {
            		 // i = n makes n - i = EVEN
            		 newDiff.get(varAssignCmd.getLv()).put(s, EVEN);
                     newDiff.get(s).put(varAssignCmd.getLv(), EVEN); 
            	 }
            	 else {
            		 // other relations now unknown. everything involves BOTTOM is BOTTOM
            		 newDiff.get(varAssignCmd.getLv()).put(s, inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
                     newDiff.get(s).put(varAssignCmd.getLv(),inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
            	 }
             
             
             }
             
         } else { // increment / decrement
             switch (inState.get(varAssignCmd.getRv())) {
                 case TOP:
                	 newState.put(varAssignCmd.getLv(), TOP);
                	 
                	 for (String s:internal.keySet()) {
                		 // everything involves BOTTOM is BOTTOM. The others relations become TOP.
                     	newDiff.get(varAssignCmd.getLv()).put(s, inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
                     	newDiff.get(s).put(varAssignCmd.getLv(),inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
                     }
                	 break;
                	 	 
                 case BOTTOM :
                	 newState.put(varAssignCmd.getLv(), BOTTOM);
                	 
                	 for (String s:internal.keySet()) {
                		 // involves BOTTOM, so all are BOTTOMS
                      	newDiff.get(varAssignCmd.getLv()).put(s, BOTTOM);
                      	newDiff.get(s).put(varAssignCmd.getLv(),BOTTOM);
                      }
                	 break;
                	 
                 // flip parity
                 case ODD: 
                	 newState.put(varAssignCmd.getLv(), EVEN);
                	 
                	 for (String s:internal.keySet()) {
                    	 if (s.equals(varAssignCmd.getRv())) {
                    		// i = n + 1 makes n - i = ODD
                    		 newDiff.get(varAssignCmd.getLv()).put(s, ODD);
                             newDiff.get(s).put(varAssignCmd.getLv(), ODD);
                    	 }
                    	 else {
                    		 if (inDiff.get(s).get(varAssignCmd.getLv()).equals(ODD)) {
                    			// fliped diff parity
                    			 newDiff.get(varAssignCmd.getLv()).put(s, EVEN);
                    			 newDiff.get(s).put(varAssignCmd.getLv(), EVEN);
                    		 } else if (inDiff.get(s).get(varAssignCmd.getLv()).equals(EVEN)) {
                    			// fliped diff parity
                    			 newDiff.get(varAssignCmd.getLv()).put(s, ODD);
                    			 newDiff.get(s).put(varAssignCmd.getLv(), ODD);
                    		 }
                    		 else {
                    			 // nothing need to be changed
                    			 
                    			 //newDiff.get(varAssignCmd.getLv()).put(s, inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
                                 //newDiff.get(s).put(varAssignCmd.getLv(),inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
                    		 }
                    		 
                    	 }
                     
                     
                     }
                	 break;
                	 
                 case EVEN: 
                	 newState.put(varAssignCmd.getLv(), ODD);
                	 
                	 for (String s:internal.keySet()) {
                    	 if (s.equals(varAssignCmd.getRv())) {
                    		// i = n + 1 makes n - i = ODD
                    		 newDiff.get(varAssignCmd.getLv()).put(s, ODD);
                             newDiff.get(s).put(varAssignCmd.getLv(), ODD);
                    	 }
                    	 else {
                    		 if (inDiff.get(s).get(varAssignCmd.getLv()).equals(ODD)) {
                    			// fliped diff parity
                    			 newDiff.get(varAssignCmd.getLv()).put(s, EVEN);
                    			 newDiff.get(s).put(varAssignCmd.getLv(), EVEN);
                    		 } else if (inDiff.get(s).get(varAssignCmd.getLv()).equals(EVEN)) {
                    			// fliped diff parity
                    			 newDiff.get(varAssignCmd.getLv()).put(s, ODD);
                    			 newDiff.get(s).put(varAssignCmd.getLv(), ODD);
                    		 }
                    		 else {
                    			 // nothing need to be changed
                    			 
                    			 //newDiff.get(varAssignCmd.getLv()).put(s, inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
                                 //newDiff.get(s).put(varAssignCmd.getLv(),inState.get(s).equals(BOTTOM)?BOTTOM:TOP);
                    		 }
                    		 
                    	 }
                     
                     
                     }
                	 break;
             }
             
            
             
         }
    }

    @Override
    public void visit(SkipCmd skipCmd) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(TrueExpr trueExpr) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(FalseExpr falseExpr) {
        newState = new HashMap<>(allBottoms);
    }

    @Override
    public void visit(IntEqualityExpr intEqualityExpr) {
    	Map<String,String> internal=inDiff.get(intEqualityExpr.getLv());
        /* assuming i != K does not add information about i's parity - no change - newState == inState */
        if (intEqualityExpr.isEqual()) { // i = K
            switch (inState.get(intEqualityExpr.getLv())) {
                // if i is bottom then i = K never holds - all variables go to bottom since there's a contradiction
                case BOTTOM:
                    newState = new HashMap<>(allBottoms);
                    newDiff = new HashMap<>(allBottomsDiff);
                    
                    
                    break;
                // if i is top then the assumption i = K adds information about i's parity - it now equals K's parity
                case TOP:
                    newState.put(intEqualityExpr.getLv(), intEqualityExpr.getVal() % 2 == 0 ? EVEN : ODD);
                    
                    // we can make it stronger here
                    
                    break;
                case ODD:
                    // if i is odd, assuming i = even K is a contradiction
                    if (intEqualityExpr.getVal() % 2 == 0) {
                        newState = new HashMap<>(allBottoms);
                        
                        newDiff = new HashMap<>(allBottomsDiff);
                    } // else - same parity, no new information learned
                    break;
                case EVEN:
                    // if i is even, assuming i = odd K is a contradiction
                    if (intEqualityExpr.getVal() % 2 == 1) {
                        newState = new HashMap<>(allBottoms);
                        
                        newDiff = new HashMap<>(allBottomsDiff);
                    } // else - same parity, no new information learned
            }
        }
    }

    @Override
    public void visit(VarEqualityExpr varEqualityExpr) {
        // Inequality case conclusion:
        // if either vars is bottom then inequality always holds and no change
        // if either vars is top then the inequality holding does not add any information about i -
        // if i = top and j = anything, then i != j does not say anything about i.
        // if j = top, assuming i != top does not say anything about i either.
        // neither is bottom - both even or odd
        // even/odd != even/odd - no new information about i

        // Equality case
        if (varEqualityExpr.isEqual()) { // i = j
            if (inState.get(varEqualityExpr.getLv()).equals(BOTTOM) || inState.get(varEqualityExpr.getRv()).equals(BOTTOM)) {
                // equality never holds, nothing equals bottom, contradiction
                newState = new HashMap<>(allBottoms);
                newDiff = new HashMap<>(allBottomsDiff);
                
            } else if (inState.get(varEqualityExpr.getLv()).equals(TOP)) { // i (top) = j => i gets j's parity
                newState.put(varEqualityExpr.getLv(), inState.get(varEqualityExpr.getRv()));
                
                newDiff.get(varEqualityExpr.getLv()).put(varEqualityExpr.getRv(), TOP);
                newDiff.get(varEqualityExpr.getRv()).put(varEqualityExpr.getLv(), TOP);
                
            } else if (inState.get(varEqualityExpr.getRv()).equals(TOP)) {  // i = j (top) => j gets i's parity
                newState.put(varEqualityExpr.getRv(), inState.get(varEqualityExpr.getLv()));
                
                newDiff.get(varEqualityExpr.getLv()).put(varEqualityExpr.getRv(), TOP);
                newDiff.get(varEqualityExpr.getRv()).put(varEqualityExpr.getLv(), TOP);
                
            } else if (!inState.get(varEqualityExpr.getLv()).equals(inState.get(varEqualityExpr.getRv()))) {
                // i and j are even/odd and different - i = j is a contradiction
                newState = new HashMap<>(allBottoms);
                newDiff = new HashMap<>(allBottomsDiff);
            }
        }
    }

    @Override
    public void visit(EvenPred evenPred) {
        // do nothing, never called
    }

    @Override
    public void visit(OddPred oddPred) {
        // do nothing, never called
    }
}
