package ast;

public class VarEqualityExpr extends Expr {
    private final String lv;
    private final String rv;
    private final boolean isEqual;

    public String getLv() {
        return lv;
    }

    public String getRv() {
        return rv;
    }

    public boolean isEqual() {
        return isEqual;
    }

    public VarEqualityExpr(String lv, String rv, boolean isEqual) {
        this.lv = lv;
        this.rv = rv;
        this.isEqual = isEqual;
    }
}
