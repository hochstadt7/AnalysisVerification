package ast;
import java.util.HashMap;
import java.util.Map;

public class CPVisitor implements Visitor {
    public static final Integer TOP = Integer.MAX_VALUE;
    public static final Integer BOTTOM = Integer.MIN_VALUE;

    private final Map<String, Integer> inState;
    private final Map<String, Map<String, Integer>> inSums;
    Map<String, Integer> newState;
    Map<String, Map<String, Integer>> newSums;
    private final Map<String, Integer> allBottoms;
    private final Map<String, Map<String, Integer>> allBottomsSums;

    private Map<String, Integer> produceAllBottoms() {
        Map<String, Integer> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            bottoms.put(var, BOTTOM);
        }
        return bottoms;
    }

    private Map<String, Map<String, Integer>> produceAllBottomsSums() {
        Map<String, Map<String, Integer>> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            Map<String, Integer> internal = new HashMap<>();
            for(String var2 : inState.keySet()) {
                if(!var.equals(var2)) {
                    internal.put(var2, BOTTOM);
                }
            }
            bottoms.put(var, internal);
        }
        return bottoms;
    }

    private void updateNewSums(String var1, String var2, Integer val) {
        newSums.get(var1).put(var2, val);
        newSums.get(var2).put(var1, val);
    }

    public CPVisitor(Map<String, Integer> inState, Map<String, Map<String, Integer>> inSums) {
        this.inState = inState;
        this.inSums = inSums;
        this.newState = new HashMap<>(inState);
        this.newSums = new HashMap<>(newSums);
        this.allBottoms = produceAllBottoms();
        this.allBottomsSums = produceAllBottomsSums();
    }

    public Map<String, Integer> getNewState() {
        return newState;
    }

    public Map<String, Map<String, Integer>> getNewSums() {
        return newSums;
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
        Map<String, Integer> internal = inSums.get(intAssignCmd.getLv());
        String var1 = intAssignCmd.getLv();

        if (intAssignCmd.isAnyNum()) { // i = ?
            newState.put(var1, TOP);
            for (String var2 : internal.keySet()) {
                // if lv -> top, then lv-var -> top <=> var !-> bottom
                updateNewSums(var1, var2, inState.get(var2).equals(BOTTOM) ? BOTTOM : TOP);
            }
        } else { // i = K
            Integer val = intAssignCmd.getVal();
            newState.put(var1, val);
            updateSumsConstAssign(internal, var1, val);
        }
    }

    // used to update all sums involving var1 with numerical value, based on var2's abs val
    private void updateSumsConstAssign(Map<String, Integer> internal, String var1, Integer val) {
        for (String var2 : internal.keySet()) {
            Integer currAbsVal2 = inState.get(var2);
            if (currAbsVal2.equals(TOP)) {
                updateNewSums(var1, var2, TOP);
            } else if (currAbsVal2.equals(BOTTOM)) {
                updateNewSums(var1, var2, BOTTOM);
            } else {
                updateNewSums(var1, var2, currAbsVal2 + val);
            }
        }
    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) {
        String var1 = varAssignCmd.getLv();
        Map<String, Integer> internal = inSums.get(var1);
        if (varAssignCmd.getType() == VarAssignCmd.AssignType.SIMPLE) { // i = j
            Integer newAbsVal1 = inState.get(varAssignCmd.getRv());
            newState.put(var1, newAbsVal1);
            for (String var2 : internal.keySet()) {
                Integer currAbsVal2 = inState.get(var2);
                boolean eitherIsBottom = newAbsVal1.equals(BOTTOM) || currAbsVal2.equals(BOTTOM);
                boolean eitherIsTop = newAbsVal1.equals(TOP) || currAbsVal2.equals(TOP);
                if (eitherIsBottom) {
                    updateNewSums(var1, var2, BOTTOM);
                } else if (eitherIsTop) { // neither is bottom
                    updateNewSums(var1, var2, TOP);
                } else {
                    updateNewSums(var1, var2, currAbsVal2 + newAbsVal1);
                }
            }
        } else { // increment / decrement
            int add = varAssignCmd.getType() == VarAssignCmd.AssignType.INCREMENT ? 1 : -1;
            Integer rvAbsVal = inState.get(varAssignCmd.getRv());
            if (rvAbsVal.equals(TOP)) {
                newState.put(var1, TOP);
                for (String var2 : internal.keySet()) {
                    Integer newSum = inState.get(var2).equals(BOTTOM) ? BOTTOM : TOP;
                    updateNewSums(var1, var2, newSum);
                }
            } else if (rvAbsVal.equals(BOTTOM)) {
                newState.put(var1, BOTTOM);
                for (String var2 : internal.keySet()) {
                    updateNewSums(var1, var2, BOTTOM);
                }
            } else {
                Integer newAbsVal1 = rvAbsVal + add;
                updateSumsConstAssign(internal, var1, newAbsVal1);
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
        String var1 = intEqualityExpr.getLv();
        Integer val = intEqualityExpr.getVal();
        Map<String, Integer> internal = inSums.get(var1);
        if (!intEqualityExpr.isEqual()) { // i != K
            if (inState.get(var1).equals(val)) { // assuming i != K where i == K is a contradiction
                newState = new HashMap<>(allBottoms);
                newSums = new HashMap<>(allBottomsSums);
            }
        } else { // i = K
            Integer currAbsVal1 = inState.get(var1);
            if (currAbsVal1.equals(BOTTOM)) {
            	newState = new HashMap<>(allBottoms);
                newSums = new HashMap<>(allBottomsSums);
            } else  { // includes the cases where i is TOP or number
            	newState.put(var1, val);
                updateSumsConstAssign(internal, var1, val);
            }
        }
    }

    @Override
    public void visit(VarEqualityExpr varEqualityExpr) {
        String lv = varEqualityExpr.getLv();
        String rv = varEqualityExpr.getRv();
        Integer prevLvVal = inState.get(lv);
        Integer prevRvVal = inState.get(rv);
        Map<String, Integer> internal = inSums.get(lv);
        if (!varEqualityExpr.isEqual()) { // i != j
            if (prevRvVal.equals(prevLvVal) && !prevRvVal.equals(TOP) && !prevRvVal.equals(BOTTOM)) { // contradiction // need to check also that not TOP or BOTTOM
                newState = new HashMap<>(allBottoms);
                newSums = new HashMap<>(allBottomsSums);
            }
        } else { // assume i = j
            if (prevRvVal.equals(BOTTOM) || prevLvVal.equals(BOTTOM)) {
                // equality never holds, nothing equals bottom, contradiction
                newState = new HashMap<>(allBottoms);
                newSums = new HashMap<>(allBottomsSums);
            } else if (prevLvVal.equals(TOP)) { // i (top) = j => i gets j's value
            	// rvVal can be odd, even, top. If top- no change
            	if (!prevRvVal.equals(TOP)) {
            		Integer newLvVal = prevRvVal;
                    newState.put(lv, newLvVal);
                    updateSumsConstAssign(internal, lv, newLvVal);
                }    
                
            }
            else if (prevRvVal.equals(TOP)) {
            	
            	//lvVal can be odd, even, top. If top- no change
            	if (!prevLvVal.equals(TOP)) {
            		internal = inSums.get(rv);
            		Integer newRvVal = prevLvVal;
                    newState.put(rv, newRvVal);
                    updateSumsConstAssign(internal, rv, newRvVal);
                }
            	
            } else if (!prevRvVal.equals(prevLvVal)) {
                // i and j are even/odd and different - i = j is a contradiction
                newState = new HashMap<>(allBottoms);
                newSums = new HashMap<>(allBottomsSums);
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
