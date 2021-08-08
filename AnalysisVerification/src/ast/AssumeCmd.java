package ast;

public class AssumeCmd extends Command {
    private final Expr expr;

    public Expr getExpr() {
        return expr;
    }

    public AssumeCmd(Expr expr) {
        this.expr = expr;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
