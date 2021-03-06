package ast;
import java.util.HashMap;
import java.util.Map;

public class CPVisitor implements Visitor {
    public static final Integer TOP = Integer.MAX_VALUE;
    public static final Integer BOTTOM = Integer.MIN_VALUE;

    private final Map<String, Integer> inState;
    Map<String, Integer> newState;
    private final Map<String, Integer> allBottoms;
    public boolean contracdiction = false;

    public Map<String, Integer> produceAllBottoms() {
        Map<String, Integer> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            bottoms.put(var, BOTTOM);
        }
        return bottoms;
    }

    public CPVisitor(Map<String, Integer> inState) {
        this.inState = inState;
        this.newState = new HashMap<>(inState);      
        this.allBottoms = produceAllBottoms();
        
    }

    public Map<String, Integer> getNewState() {
        return newState;
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
        String var1 = intAssignCmd.getLv();
        if (intAssignCmd.isAnyNum()) { // i = ?
            newState.put(var1, TOP);            
        } else { // i = K
            Integer val = intAssignCmd.getVal();
            newState.put(var1, val);
        }
    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) {
        String var1 = varAssignCmd.getLv();
        if (varAssignCmd.getType() == VarAssignCmd.AssignType.SIMPLE) { // i = j
            Integer newAbsVal1 = inState.get(varAssignCmd.getRv());
            newState.put(var1, newAbsVal1);
        } else { // increment / decrement
            int add = varAssignCmd.getType() == VarAssignCmd.AssignType.INCREMENT ? 1 : -1;
            Integer rvAbsVal = inState.get(varAssignCmd.getRv());
            if (rvAbsVal.equals(TOP)) {
                newState.put(var1, TOP);
            } else if (rvAbsVal.equals(BOTTOM)) {
                newState.put(var1, BOTTOM);
            } else {
                Integer newAbsVal1 = rvAbsVal + add;
                newState.put(var1, newAbsVal1);
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
        if (!intEqualityExpr.isEqual()) { // i != K
            if (inState.get(var1).equals(val)) { // assuming i != K where i == K is a contradiction
                this.contracdiction = true;
                newState = new HashMap<>(allBottoms);
            }
        } else { // i = K
            Integer currAbsVal1 = inState.get(var1);
            if (currAbsVal1.equals(BOTTOM)) {
                this.contracdiction = true;
            	newState = new HashMap<>(allBottoms);
            } else  { // includes the cases where i is TOP or number
            	newState.put(var1, val);
            }
        }
    }

    @Override
    public void visit(VarEqualityExpr varEqualityExpr) {
        String lv = varEqualityExpr.getLv();
        String rv = varEqualityExpr.getRv();
        Integer prevLvVal = inState.get(lv);
        Integer prevRvVal = inState.get(rv);
        if (!varEqualityExpr.isEqual()) { // i != j
            if (prevRvVal.equals(prevLvVal) &&
                    !prevRvVal.equals(TOP) &&
                    !prevRvVal.equals(BOTTOM)
            ) { // if i = j and they both have the same numerical value - contradiction
                this.contracdiction = true;
                newState = new HashMap<>(allBottoms);
            }
        } else { // assume i = j
            if (prevRvVal.equals(BOTTOM) || prevLvVal.equals(BOTTOM)) {
                // equality never holds, nothing equals bottom, contradiction
                newState = new HashMap<>(allBottoms);
            } else if ((prevLvVal.equals(TOP) && !prevRvVal.equals(TOP))) { // i (top) = j => i gets j's value
                newState.put(lv, prevRvVal);
            }
            else if (prevRvVal.equals(TOP) && !prevLvVal.equals(TOP)) { // i = j (top) => j gets i's value
                newState.put(rv, prevLvVal);
            } else if (!prevRvVal.equals(prevLvVal)) {
                // i and j are even/odd and different - i = j is a contradiction
                this.contracdiction = true;
                newState = new HashMap<>(allBottoms);
            }
        }
    }
}
