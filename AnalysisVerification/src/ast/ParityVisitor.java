package ast;

import java.util.HashMap;
import java.util.Map;

public class ParityVisitor implements Visitor {
    private final Map<String, String> inState;
    Map<String, String> newState;

    public ParityVisitor(Map<String, String> inState) {
        this.inState = inState;
        this.newState = new HashMap<>(inState);
    }

    public Map<String, String> getNewState() {
        return newState;
    }

    @Override
    public void visit(AssertCmd assertCmd) {

    }

    @Override
    public void visit(AssumeCmd assumeCmd) {

    }

    @Override
    public void visit(IntAssignCmd intAssignCmd) {

    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) {

    }

    @Override
    public void visit(SkipCmd skipCmd) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(TrueExpr trueExpr) {

    }

    @Override
    public void visit(FalseExpr falseExpr) {

    }

    @Override
    public void visit(IntEqualityExpr intEqualityExpr) {

    }

    @Override
    public void visit(VarEqualityExpr varEqualityExpr) {

    }

    @Override
    public void visit(EvenPred evenPred) {

    }

    @Override
    public void visit(OddPred oddPred) {

    }
}
