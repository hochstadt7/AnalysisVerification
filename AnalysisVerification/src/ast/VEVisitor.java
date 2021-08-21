package ast;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class VEVisitor implements Visitor {
    private final Set<VariableEquality> inState;
    private Set<VariableEquality> newState;
    public boolean contracdiction = false;

    public VEVisitor(Set<VariableEquality> inState) {
        this.inState = inState;
        this.newState = new HashSet<>(inState);
    }

    public Set<VariableEquality> getNewState() {
        return newState;
    }

    private void explicateState() {
        Set<VariableEquality> newVE = new HashSet<>();
        for (VariableEquality ve1 : newState) {
            for (VariableEquality ve2 : newState) {
                if (ve1.getRv().equals(ve2.getLv())) { // a=b, b=c in state
                    VariableEquality reducedVe = new VariableEquality(ve1.getLv(), ve2.getRv()); // a=c
                    if (!newState.contains(reducedVe)) {
                        newVE.add(reducedVe);
                    }
                }
            }
        }
        if (newVE.size() > 0) {
            newState.addAll(newVE);
            explicateState();
        }
    }

    @Override
    public void visit(AssertCmd assertCmd) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(AssumeCmd assumeCmd) {
        explicateState();
        assumeCmd.getExpr().accept(this);
        explicateState();
    }

    @Override
    public void visit(IntAssignCmd intAssignCmd) { // i := K or i := ?
        explicateState();
        newState.removeIf(ve -> ve.involvesVar(intAssignCmd.getLv()));
        explicateState();
    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) { // i := j (+-1)
        if (varAssignCmd.getType() == VarAssignCmd.AssignType.SIMPLE) { // i := j
            explicateState();
            newState.removeIf(ve -> ve.involvesVar(varAssignCmd.getLv()));
            newState.add(new VariableEquality(varAssignCmd.getLv(), varAssignCmd.getRv()));
            explicateState();
        }
        else {
            explicateState();
            newState.removeIf(ve -> ve.involvesVar(varAssignCmd.getLv()));
            explicateState();
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
        newState = new HashSet<>();
    }

    @Override
    public void visit(IntEqualityExpr intEqualityExpr) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(VarEqualityExpr varEqualityExpr) {
        VariableEquality exprVe = new VariableEquality(varEqualityExpr.getLv(), varEqualityExpr.getRv());
        if (varEqualityExpr.isEqual()) { // assume i = j
            newState.add(exprVe);
        } else { // assume i != j
            if (newState.contains(exprVe)) { // contradiction
                this.contracdiction = true;
                newState = new HashSet<>();
            }
        }
    }
}
