package ast;

import java.util.HashMap;

public interface Visitor {
    // for commands and expressions (in assume)
    void visit(AssertCmd assertCmd);
    void visit(AssumeCmd assumeCmd);
    void visit(IntAssignCmd intAssignCmd);
    void visit(VarAssignCmd varAssignCmd);
    void visit(SkipCmd skipCmd);

    void visit(TrueExpr trueExpr);
    void visit(FalseExpr falseExpr);
    void visit(IntEqualityExpr intEqualityExpr);
    void visit(VarEqualityExpr varEqualityExpr);
}

