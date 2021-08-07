package ast;

public class IntAssignCmd extends Command {
    private final String lv;
    private final Integer val; // null if the command is var := ?
    private boolean anyNum = false;

    public String getLv() {
        return lv;
    }

    public Integer getVal() {
        return val;
    }

    public boolean isAnyNum() { return anyNum; }

    public IntAssignCmd(String lv, Integer val) {
        this.lv = lv;
        this.val = val;
        if (val == null) {
            this.anyNum = true;
        }
    }
}
