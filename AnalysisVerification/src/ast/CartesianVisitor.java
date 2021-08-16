package ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CartesianVisitor implements Visitor {

    private final Map<String, CartesianProduct> inState;
    Map<String, CartesianProduct> newState;
    private final Map<String, CartesianProduct> allBottoms;

    private Map<String, CartesianProduct> produceAllBottoms() {
        Map<String, CartesianProduct> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            CartesianProduct currCartesianProduct = inState.get(var);
            bottoms.put(var, new CartesianProduct(new ParityVisitor(currCartesianProduct.getInStateParity(), null)
                    .produceAllBottoms(), new ParityVisitor(currCartesianProduct.getInStateParity(),null)
                    .produceAllBottomsDiff(), new HashSet<VariableEquality>(),
                    new CPVisitor(currCartesianProduct.getInStateCP()).produceAllBottoms()));

        }
        return bottoms;
    }

    public CartesianVisitor(Map<String, CartesianProduct> inState) {
        this.inState = inState;
        this.newState = new HashMap<>();
        this.allBottoms = produceAllBottoms();
    }

    public Map<String, CartesianProduct> getNewState() {
        return newState;
    }

    @Override
    public void visit(AssertCmd assertCmd) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(AssumeCmd assumeCmd) {
        assumeCmd.getExpr().accept(this);
    }

    public void updateNewState(Command command){
        for (String var : inState.keySet()){
            CartesianProduct currCartesianProduct = inState.get(var);
            ParityVisitor v = new ParityVisitor(currCartesianProduct.getInStateParity(),currCartesianProduct.getInDiffParity());
            command.accept(v);
            CPVisitor cpVisitor = new CPVisitor(currCartesianProduct.getInStateCP());
            command.accept(v);
            VEVisitor veVisitor= new VEVisitor(currCartesianProduct.getInStateVE());
            command.accept(veVisitor);
            newState.put(var, new CartesianProduct(v.getNewState(), v.getNewDiff(), veVisitor.getNewState(), cpVisitor.getNewState()));
        }
    }

    @Override
    public void visit(IntAssignCmd intAssignCmd) {
        updateNewState(intAssignCmd);
    }

    @Override
    public void visit(VarAssignCmd varAssignCmd) {
        updateNewState(varAssignCmd);
    }

    @Override
    public void visit(SkipCmd skipCmd) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(TrueExpr trueExpr) {
        /* no change - newState == inState */
    }

    @Override
    public void visit(FalseExpr falseExpr) {
        newState = new HashMap<>(allBottoms);
    }

    @Override
    public void visit(IntEqualityExpr intEqualityExpr) {
        updateNewState(intEqualityExpr);
    }

    @Override
    public void visit(VarEqualityExpr varEqualityExpr) {
        updateNewState(varEqualityExpr);
    }
}
