package ast;

public class OddPred extends Predicate {
    private final String id;

    public OddPred(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
