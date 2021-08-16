import ast.CPVisitor;
import ast.CartesianProduct;
import ast.CartesianVisitor;
import ast.ParityVisitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import ast.Command;
import ast.VariableEquality;

public class CartesianAnalysis {

    public CartesianVisitor applyAbstractFunction(Map<String, CartesianProduct> inState, Command command) {
        CartesianVisitor v = new CartesianVisitor(inState);
        command.accept(v);
        return v;
    }

    public static CartesianProduct joinPointWise(CartesianProduct value1, CartesianProduct value2) {

        Map<String, String> joinParity = ParityAnalysis.join(value1.getInStateParity(), value2.getInStateParity());
        Map<String, Map<String, String>> joinDiff = ParityAnalysis.joinRelState(value1.getInDiffParity(), value2.getInDiffParity());
        Set<VariableEquality> joinVE = VEAnalysis.join(value1.getInStateVE(), value2.getInStateVE());
        Map<String, Integer> joinCP = CPAnalysis.join(value1.getInStateCP(), value2.getInStateCP());

        return new CartesianProduct(joinParity, joinDiff, joinVE, joinCP);
    }


    public static Map<String, CartesianProduct> join(Map<String, CartesianProduct> state1, Map<String, CartesianProduct> state2){
        Map<String, CartesianProduct> output = new HashMap<>();
        // join pointwise
        for (String varName : state1.keySet()) {
            output.put(varName, joinPointWise(state1.get(varName), state2.get(varName)));
        }
        return output;
    }
}
