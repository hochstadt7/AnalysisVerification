package ast;

import java.util.HashMap;
import java.util.Map;

public class ParityVisitor implements Visitor {
    public static final String ODD = "ODD";
    public static final String EVEN = "EVEN";
    public static final String TOP = "TOP";
    public static final String BOTTOM = "BOTTOM";

    private final Map<String, String> inState;
    Map<String, String> newState;
    private final Map<String, String> allBottoms;

    private Map<String, String> produceAllBottoms() {
        Map<String, String> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            bottoms.put(var, BOTTOM);
        }
        return bottoms;
    }

    public ParityVisitor(Map<String, String> inState) {
        this.inState = inState;
        this.newState = new HashMap<>(inState);
        this.allBottoms = produceAllBottoms();
    }

    public Map<String, String> getNewState() {
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
        if (intAssignCmd.isAnyNum()) {
            newState.put(intAssignCmd.getLv(), TOP);
        } else {
            String newAbsVal = intAssignCmd.getVal() % 2 == 0 ? EVEN : ODD;
            newState.put(intAssignCmd.getLv(), newAbsVal);
        }
    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) {
         if (varAssignCmd.getType() == VarAssignCmd.AssignType.SIMPLE) {
             newState.put(varAssignCmd.getLv(), inState.get(varAssignCmd.getRv()));
         } else { // increment / decrement
             switch (inState.get(varAssignCmd.getRv())) {
                 case TOP -> newState.put(varAssignCmd.getLv(), TOP);
                 case BOTTOM -> newState.put(varAssignCmd.getLv(), BOTTOM);
                 // flip parity
                 case ODD -> newState.put(varAssignCmd.getLv(), EVEN);
                 case EVEN -> newState.put(varAssignCmd.getLv(), ODD);
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
        /* assuming i != K does not add information about i's parity - no change - newState == inState */
        if (intEqualityExpr.isEqual()) { // i = K
            switch (inState.get(intEqualityExpr.getLv())) {
                // if i is bottom then i = K never holds - all variables go to bottom since there's a contradiction
                case BOTTOM:
                    newState = new HashMap<>(allBottoms);
                    break;
                // if i is top then the assumption i = K adds information about i's parity - it now equals K's parity
                case TOP:
                    newState.put(intEqualityExpr.getLv(), intEqualityExpr.getVal() % 2 == 0 ? EVEN : ODD);
                    break;
                case ODD:
                    // if i is odd, assuming i = even K is a contradiction
                    if (intEqualityExpr.getVal() % 2 == 0) {
                        newState = new HashMap<>(allBottoms);
                    } // else - same parity, no new information learned
                    break;
                case EVEN:
                    // if i is even, assuming i = odd K is a contradiction
                    if (intEqualityExpr.getVal() % 2 == 1) {
                        newState = new HashMap<>(allBottoms);
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
            } else if (inState.get(varEqualityExpr.getLv()).equals(TOP)) { // i (top) = j => i gets j's parity
                newState.put(varEqualityExpr.getLv(), inState.get(varEqualityExpr.getRv()));
            } else if (inState.get(varEqualityExpr.getRv()).equals(TOP)) {  // i = j (top) => j gets i's parity
                newState.put(varEqualityExpr.getRv(), inState.get(varEqualityExpr.getLv()));
            } else if (!inState.get(varEqualityExpr.getLv()).equals(inState.get(varEqualityExpr.getRv()))) {
                // i and j are even/odd and different - i = j is a contradiction
                newState = new HashMap<>(allBottoms);
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
