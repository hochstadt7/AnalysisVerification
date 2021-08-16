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
    public Map<String, Map<String, String>> produceAllBottomsDiff() {
        Map<String, Map<String, String>> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            Map<String,String> internal = new HashMap<>();
            for(String var2 : inState.keySet()) {
                if(!var.equals(var2)) {
                    internal.put(var2, BOTTOM);
                }
            }
            bottoms.put(var, internal);
        }
        return bottoms;
    }

    private void updateNewDiff(String var1, String var2, String val) {
        newDiff.get(var1).put(var2, val);
        newDiff.get(var2).put(var1, val);
    }

    public ParityVisitor(Map<String, String> inState, Map<String, Map<String, String>> inDiff) {
        this.inState = inState;
        this.inDiff = inDiff;
        this.newState = new HashMap<>(inState);
        this.newDiff = new HashMap<>(inDiff);
        this.allBottoms = produceAllBottoms();
        this.allBottomsDiff = produceAllBottomsDiff();
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
        Map<String,String> internal = inDiff.get(intAssignCmd.getLv());
        String var1 = intAssignCmd.getLv();

        if (intAssignCmd.isAnyNum()) { // i = ?
            newState.put(var1, TOP);
            for (String var2 : internal.keySet()) {
                // if lv -> top, then lv-var -> top <=> var !-> bottom
                updateNewDiff(var1, var2, inState.get(var2).equals(BOTTOM) ? BOTTOM : TOP);
            }
        } else { // i = K
            String newAbsVal1 = intAssignCmd.getVal() % 2 == 0 ? EVEN : ODD;
            newState.put(var1, newAbsVal1);

            for (String var2 : internal.keySet()) {
                String currAbsVal2 = inState.get(var2);
                switch (currAbsVal2) {
                    case EVEN ->
                            updateNewDiff(var1, var2, newAbsVal1.equals(EVEN) ? EVEN : ODD);
                    case ODD ->
                            updateNewDiff(var1, var2, newAbsVal1.equals(ODD) ? EVEN : ODD);
                    default -> // var2 -> bot, top
                            updateNewDiff(var1, var2, currAbsVal2.equals(BOTTOM) ? BOTTOM : TOP);
                }
            }
        }
    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) {
        String lv = varAssignCmd.getLv();
        String rv = varAssignCmd.getRv();
        Map<String, String> internal = inDiff.get(lv);
        String newAbsVal1 = inState.get(rv);
        if (varAssignCmd.getType() == VarAssignCmd.AssignType.SIMPLE) { // i = j
            newState.put(lv, newAbsVal1);
            for (String var2 : internal.keySet()) {

                String currAbsVal2 = inState.get(var2);
                if (var2.equals(rv)) {
                    // i = n makes n - i = EVEN
                    updateNewDiff(lv, var2, inState.get(rv).equals(BOTTOM)? BOTTOM : EVEN); // even if n is Bottom???
                }
                else {
                    // update by prev_gap
                    String prev_gap = inDiff.get(rv).get(var2);
                    if (prev_gap.equals(EVEN) || prev_gap.equals(ODD)){
                        updateNewDiff(lv, var2, prev_gap.equals(EVEN) ? EVEN : ODD);
                        continue;
                    }

                    boolean bothEvenOrOdd = (newAbsVal1.equals(EVEN) || newAbsVal1.equals(ODD)) &&
                            (currAbsVal2.equals(EVEN) || currAbsVal2.equals(ODD));
                    if (bothEvenOrOdd) {
                        if (newAbsVal1.equals(currAbsVal2)) {
                            updateNewDiff(lv, var2, EVEN);
                        } else {
                            updateNewDiff(lv, var2, ODD);
                        }
                    } else {
                        if (currAbsVal2.equals(BOTTOM) || newAbsVal1.equals(BOTTOM))
                            updateNewDiff(lv, var2, BOTTOM);
                        else
                            updateNewDiff(lv, var2, TOP);
                    }
                }
            }

        } else { // increment / decrement // need to set i - j = odd!
            switch (inState.get(rv)) {
                case EVEN -> newState.put(lv, ODD);
                case ODD -> newState.put(lv, EVEN);
                default -> newState.put(lv, inState.get(rv));
            }
            for (String var2 : internal.keySet()) {
                String currAbsVal2 = inState.get(var2);
                if (var2.equals(rv)) {
                    // i = n + 1 makes n - i = ODD
                    updateNewDiff(lv, var2, inState.get(rv).equals(BOTTOM)? BOTTOM : ODD); // odd if var2.absVal is BOTTOM????
                }
                else {
                    String prev_gap = inDiff.get(rv).get(var2);
                    if (prev_gap.equals(EVEN) || prev_gap.equals(ODD)){
                        updateNewDiff(lv, var2, prev_gap.equals(EVEN) ? ODD : EVEN);
                        continue;
                    }

                    boolean bothEvenOrOdd = (newAbsVal1.equals(EVEN) || newAbsVal1.equals(ODD)) &&
                            (currAbsVal2.equals(EVEN) || currAbsVal2.equals(ODD));
                    if (bothEvenOrOdd) {
                        if (newAbsVal1.equals(currAbsVal2)) {
                            updateNewDiff(lv, var2, ODD);
                        } else {
                            updateNewDiff(lv, var2, EVEN);
                        }
                    } else {
                        if (currAbsVal2.equals(BOTTOM) || newAbsVal1.equals(BOTTOM))
                            updateNewDiff(lv, var2, BOTTOM);
                        else
                            updateNewDiff(lv, var2, TOP);
                    }
                }
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
        Map<String,String> internal = inDiff.get(var1);

        /* assuming i != K does not add information about i's parity - no change - newState == inState */
        if (intEqualityExpr.isEqual()) { // i = K
            String prevAbsVal1 = inState.get(var1);
            switch (prevAbsVal1) {
                // if i is bottom then i = K never holds - all variables go to bottom since there's a contradiction
                case BOTTOM:
                    newState = new HashMap<>(allBottoms);
                    newDiff = new HashMap<>(allBottomsDiff);
                    break;
                // if i was top then the assumption i = K adds information about i's parity - it now equals K's parity
                case TOP:

                    String newAbsVal1 = intEqualityExpr.getVal() % 2 == 0 ? EVEN : ODD;
                    newState.put(var1, newAbsVal1);
                    for (String var2 : internal.keySet()) {
                        String currAbsVal2 = inState.get(var2);
                        switch (currAbsVal2) {
                            case EVEN ->
                                    updateNewDiff(var1, var2, newAbsVal1.equals(EVEN) ? EVEN : ODD);
                            case ODD ->
                                    updateNewDiff(var1, var2, newAbsVal1.equals(ODD) ? EVEN : ODD);
                        }
                    }
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
        String lv = varEqualityExpr.getLv();
        String rv = varEqualityExpr.getRv();
        Map<String, String> internal = inDiff.get(lv);
        // Inequality case conclusion:
        // if either vars is bottom then inequality always holds and no change
        // if either vars is top then the inequality holding does not add any information about i -
        // if i = top and j = anything, then i != j does not say anything about i.
        // if j = top, assuming i != top does not say anything about i either.
        // neither is bottom - both even or odd
        // even/odd != even/odd - no new information about i

        // Equality case
        if (varEqualityExpr.isEqual()) { // i = j
            String prevLvVal = inState.get(lv);
            String prevRvVal = inState.get(rv);
            if (prevRvVal.equals(BOTTOM) || prevLvVal.equals(BOTTOM)) {
                // equality never holds, nothing equals bottom, contradiction
                newState = new HashMap<>(allBottoms);
                newDiff = new HashMap<>(allBottomsDiff);
            } else if (prevLvVal.equals(TOP)) { // i (top) = j => i gets j's parity
                String newLvVal = prevRvVal;
                newState.put(lv, newLvVal);
                // rvVal can be odd, even, top
                for (String var2 : internal.keySet()) {

                    if (var2.equals(rv)){
                        updateNewDiff(lv, var2, EVEN);
                        continue;
                    }
                    // update by prev_gap
                    String prev_gap = inDiff.get(rv).get(var2);
                    if (prev_gap.equals(EVEN) || prev_gap.equals(ODD)){
                        updateNewDiff(lv, var2, prev_gap.equals(EVEN) ? EVEN : ODD);
                        continue;
                    }

                    String currAbsVal2 = inState.get(var2);
                    switch (newLvVal) {
                        // prevLvVal = newLvVal = top => no change
                        case EVEN:
                            if (currAbsVal2.equals(EVEN) || currAbsVal2.equals(ODD)) {
                                updateNewDiff(lv, var2, currAbsVal2.equals(EVEN) ? EVEN : ODD);
                            } else {
                                updateNewDiff(lv, var2, currAbsVal2.equals(BOTTOM) ? BOTTOM : TOP);
                            }
                        case ODD:
                            if (currAbsVal2.equals(EVEN) || currAbsVal2.equals(ODD)) {
                                updateNewDiff(lv, var2, currAbsVal2.equals(ODD) ? EVEN : ODD);
                            } else {
                                updateNewDiff(lv, var2, currAbsVal2.equals(BOTTOM) ? BOTTOM : TOP);
                            }

                    }
                }
            } else if (prevRvVal.equals(TOP)) {  // i = j (top) => j gets i's parity
                String newRvVal = prevLvVal;
                internal = inDiff.get(rv);

                newState.put(rv, newRvVal);
                for (String var2 : internal.keySet()) {

                    if (var2.equals(rv)){
                        updateNewDiff(rv, var2, EVEN);
                        continue;
                    }
                    String prev_gap = inDiff.get(lv).get(var2);
                    if (prev_gap.equals(EVEN) || prev_gap.equals(ODD)) {
                        updateNewDiff(rv, var2, prev_gap.equals(EVEN) ? EVEN : ODD);
                        continue;
                    }

                    String currAbsVal2 = inState.get(var2);
                    switch (newRvVal) {
                        // prevLvVal = newLvVal = top => no change
                        case EVEN:
                            if (currAbsVal2.equals(EVEN) || currAbsVal2.equals(ODD)) {
                                updateNewDiff(rv, var2, currAbsVal2.equals(EVEN) ? EVEN : ODD);
                            } else {
                                updateNewDiff(rv, var2, currAbsVal2.equals(BOTTOM) ? BOTTOM : TOP);
                            }
                        case ODD:
                            if (currAbsVal2.equals(EVEN) || currAbsVal2.equals(ODD)) {
                                updateNewDiff(rv, var2, currAbsVal2.equals(ODD) ? EVEN : ODD);
                            } else {
                                updateNewDiff(rv, var2, currAbsVal2.equals(BOTTOM) ? BOTTOM : TOP);
                            }

                    }
                }
            } else if (!prevRvVal.equals(prevLvVal)) {
                // i and j are even/odd and different - i = j is a contradiction
                newState = new HashMap<>(allBottoms);
                newDiff = new HashMap<>(allBottomsDiff);
            }
        }
    }
}
