package ast;

public class TrueExpr extends Expr {
    public TrueExpr() {
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
