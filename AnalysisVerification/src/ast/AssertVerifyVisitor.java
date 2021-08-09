package ast;

import java.util.*;

public class AssertVerifyVisitor {
    Map<String, String> currState;

    public AssertVerifyVisitor(Map<String, String> currState) {
        this.currState = currState;
    }

    public boolean visit(AssertCmd assertCmd) {
        List<List<Predicate>> dnf = assertCmd.getDNF();
        // for dnf to hold, we need one and clause that holds
        boolean anyTrue = false;
        for (List<Predicate> andClause : dnf) {
            boolean anyFalse = false;
            for (Predicate pred : andClause) {
                if (!pred.acceptVerifier(this)) {
                    anyFalse = true;
                    break;
                }
            } if (!anyFalse) { // and clause holds
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
