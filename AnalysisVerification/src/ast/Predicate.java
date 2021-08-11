package ast;

public abstract class Predicate {
    public Predicate() {
    }
    abstract public String getId();
    abstract public boolean acceptVerifier(AssertVerifyVisitor v);
}
