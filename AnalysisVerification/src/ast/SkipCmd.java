package ast;

public class SkipCmd extends Command{
    public SkipCmd() {
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
}
