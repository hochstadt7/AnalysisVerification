package ast;

public class OddPred extends Predicate {
    private final String id;

    public OddPred(String id) {
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
