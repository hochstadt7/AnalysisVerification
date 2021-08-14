package ast;
import java.util.Objects;

public class VariableEquality {
    private final String lv;
    private final String rv;

    public VariableEquality(String lv, String rv) {
        this.lv = lv;
        this.rv = rv;
    }

    public String getLv() {
        return lv;
    }

    public String getRv() {
        return rv;
    }

    public boolean involvesVar(String var) {
        return rv.equals(var) || lv.equals(var);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VariableEquality)) {
            return false;
        }
        VariableEquality other = (VariableEquality) o;
        return (other.lv.equals(this.lv) && other.rv.equals(this.rv)) ||
                (other.lv.equals(this.rv) && other.rv.equals(this.lv));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lv, this.rv) + Objects.hash(this.rv, this.lv);
    }
}
