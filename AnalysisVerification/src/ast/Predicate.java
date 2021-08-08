package ast;

public abstract class Predicate {
    public Predicate() {
    }
    abstract public boolean acceptVerifier(AssertVerifyVisitor v);
}
