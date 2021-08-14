import ast.VEVisitor;
import ast.Command;
import ast.VariableEquality;

import java.util.HashSet;
import java.util.Set;

public class VEAnalysis {
    VEVisitor applyAbstractFunction(Set<VariableEquality> inState, Command command) {
        VEVisitor v = new VEVisitor(inState);
        command.accept(v);
        return v;
    }

    public Set<VariableEquality> join(Set<VariableEquality> state1, Set<VariableEquality> state2) {
        Set<VariableEquality> output = new HashSet<>();
        for (VariableEquality ve : state1) {
            if (state2.contains(ve)) {
                output.add(ve);
            }
        }
        return output;
    }
}
