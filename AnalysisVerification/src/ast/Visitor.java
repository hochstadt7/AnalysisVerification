package ast;

import java.util.HashMap;

public interface Visitor {
    public void visit(AssertCmd assertCmd);
    public void visit(AssumeCmd assumeCmd);
    public void visit(IntAssignCmd intAssignCmd);
    public void visit(VarAssignCmd varAssignCmd);
    public void visit(SkipCmd skipCmd);

    public void visit(TrueExpr trueExpr);
    public void visit(FalseExpr falseExpr);
    public void visit(IntEqualityExpr intEqualityExpr);
    public void visit(VarEqualityExpr varEqualityExpr);

    public void visit(EvenPred evenPred);
    public void visit(OddPred oddPred);
}

