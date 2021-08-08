package ast;

public class FalseExpr extends Expr {
    public FalseExpr() {
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
