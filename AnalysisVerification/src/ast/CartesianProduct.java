package ast;

import java.util.Map;
import java.util.Set;

public class CartesianProduct {

    private final Map<String, String> inStateParity;
    private final Map<String, Map<String, String>> inDiffParity;
    private final Set<VariableEquality> inStateVE;
    private final Map<String, Integer> inStateCP;

    public Map<String, String> getInStateParity() {
        return inStateParity;
    }

    public Map<String, Map<String, String>> getInDiffParity() {
        return inDiffParity;
    }

    public Set<VariableEquality> getInStateVE() {
        return inStateVE;
    }

    public Map<String, Integer> getInStateCP() {
        return inStateCP;
    }

    public CartesianProduct(Map<String, String> inStateParity, Map<String, Map<String, String>> inDiffParity, Set<VariableEquality> inStateVE, Map<String, Integer> inStateCP) {
        this.inStateParity = inStateParity;
        this.inDiffParity = inDiffParity;
        this.inStateVE = inStateVE;
        this.inStateCP = inStateCP;
    }

}
