package ast;
import java.util.List;

public class AssertCmd extends Command {
    private final List<List<Predicate>> dnf;

    public List<List<Predicate>> getDNF() {
        return dnf;
    }

    public AssertCmd(List<List<Predicate>> dnf) {
        this.dnf = dnf;
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }

    public boolean acceptVerifier(AssertVerifyVisitor v) {
        return v.visit(this);
    }
}
