import ast.CartesianProduct;
import ast.CartesianVisitor;
import ast.VEVisitor;
import ast.Command;
import ast.VariableEquality;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CartesianAnalysis {
    CartesianVisitor applyAbstractFunction(Map<String, CartesianProduct> inState, Command command) {
        CartesianVisitor v = new CartesianVisitor(inState);
        command.accept(v);
        return v;
    }

    public CartesianVisitor join(CartesianVisitor state1, CartesianVisitor state2) {
        CartesianVisitor output = new CartesianVisitor();
        /*
        logic
         */
        return output;
    }
}
