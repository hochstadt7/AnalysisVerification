package ast;

public abstract class Expr {
    public Expr() {
    }

    abstract public void accept(Visitor v);
}
