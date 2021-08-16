package ast;

public class VarAssignCmd extends Command {
       public enum AssignType { SIMPLE, INCREMENT, DECREMENT }

    private final String lv;
    private final String rv;
    private AssignType type;

    public String getLv() {
        return lv;
    }

    public String getRv() {
        return rv;
    }

    public AssignType getType() {
        return type;
    }

    public VarAssignCmd(String lv, String rv, Integer add) {
        this.lv = lv;
        this.rv = rv;
        switch (add) {
            case 1:
                this.type = AssignType.INCREMENT;
                break;
            case 0:
                this.type = AssignType.SIMPLE;
                break;
            case -1:
                this.type = AssignType.DECREMENT;
                break;
        }
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
