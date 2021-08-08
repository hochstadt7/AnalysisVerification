package ast;

public abstract class Command {
    public Command() {
    }

    abstract public void accept(Visitor v);

}
