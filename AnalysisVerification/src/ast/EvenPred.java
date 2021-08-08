package ast;

public class EvenPred extends Predicate {
    private final String id;

    public EvenPred(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean acceptVerifier(AssertVerifyVisitor v) {
        return v.visit(this);
    }
}
