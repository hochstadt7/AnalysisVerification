package ast;

import java.util.List;

public class SumPred extends Predicate{
    private List<String> rightTerms;
    private List<String> leftTerms;

    public SumPred(List<String> leftTerms, List<String> rightTerms) {
        this.rightTerms = rightTerms;
        this.leftTerms = leftTerms;
    }

    public List<String> getRightTerms() {
        return rightTerms;
    }

    public List<String> getLeftTerms() {
        return leftTerms;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean acceptVerifier(AssertVerifyVisitor v) {
        return v.visit(this);
    }
}
