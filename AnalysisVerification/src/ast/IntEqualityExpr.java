package ast;

public class IntEqualityExpr extends Expr{
     private final String lv;
     private final Integer val;
     private final boolean isEqual;

     public String getLv() {
         return lv;
     }

    public Integer getVal() {
        return val;
    }

    public boolean isEqual() {
         return isEqual;
     }

     public IntEqualityExpr(String lv, Integer val, boolean isEqual) {
         this.lv = lv;
         this.val = val;
         this.isEqual = isEqual;
     }


    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
