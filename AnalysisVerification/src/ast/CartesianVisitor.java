package ast;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CartesianVisitor implements Visitor {

    private final Map<String, CartesianProduct> inState;
    Map<String, CartesianProduct> newState;
    private final Map<String, CartesianProduct> allBottoms;

    // bottom CartesianProduct generation
    public static CartesianProduct bottomProduct(Set<String> varList){

        //ParityVisitor.produceAllBottoms
        Map<String, String> bottomsParity = new HashMap<>();
        for (String var : varList) {
            bottomsParity.put(var, ParityVisitor.BOTTOM);
        }

        //ParityVisitor.produceAllBottomsDiff
        Map<String, Map<String, String>> bottomsDiff = new HashMap<>();
        for (String var : varList) {
            Map<String,String> internal = new HashMap<>();
            for(String var2 : varList) {
                if(!var.equals(var2)) {
                    internal.put(var2, ParityVisitor.BOTTOM);
                }
            }
            bottomsDiff.put(var, internal);
        }

        //CPVisitor.produceAllBottoms
        Map<String, Integer> bottomsCP = new HashMap<>();
        for (String var : varList) {
            bottomsCP.put(var, CPVisitor.BOTTOM);
        }

        //VEVisitor.produceAllBottoms
        Set<VariableEquality> bottomsVE = new HashSet<>();

        return new CartesianProduct(bottomsParity, bottomsDiff, bottomsVE, bottomsCP);
    }

    // quite similar to Manager.initializeCartesianState
    private Map<String, CartesianProduct> produceAllBottoms() {
        Map<String, CartesianProduct> bottoms = new HashMap<>();
        for (String var : inState.keySet()) {
            bottoms.put(var, CartesianVisitor.bottomProduct(inState.keySet()));
        }
        return bottoms;
    }

    public CartesianVisitor(Map<String, CartesianProduct> inState) {
        this.inState = inState;
        this.newState = new HashMap<>(inState);
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
            ParityVisitor v = new ParityVisitor(currCartesianProduct.getInStateParity(),
                    currCartesianProduct.getInDiffParity());
            command.accept(v);
            CPVisitor cpVisitor = new CPVisitor(currCartesianProduct.getInStateCP());
            command.accept(v);
            VEVisitor veVisitor= new VEVisitor(currCartesianProduct.getInStateVE());
            command.accept(veVisitor);
            newState.put(var, new CartesianProduct(v.getNewState(), v.getNewDiff(),
                    veVisitor.getNewState(), cpVisitor.getNewState()));
        }
    }

    public void updateNewStateExpr(Expr command){
        for (String var : inState.keySet()){
            CartesianProduct currCartesianProduct = inState.get(var);
            ParityVisitor v = new ParityVisitor(currCartesianProduct.getInStateParity(),
                    currCartesianProduct.getInDiffParity());
            command.accept(v);
            CPVisitor cpVisitor = new CPVisitor(currCartesianProduct.getInStateCP());
            command.accept(v);
            VEVisitor veVisitor= new VEVisitor(currCartesianProduct.getInStateVE());
            command.accept(veVisitor);
            newState.put(var, new CartesianProduct(v.getNewState(), v.getNewDiff(),
                    veVisitor.getNewState(), cpVisitor.getNewState()));
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

    // I am not sure assume is that simple. if one analysis moved to bottoms, all others should too?
    @Override
    public void visit(IntEqualityExpr intEqualityExpr) {
        updateNewStateExpr(intEqualityExpr);
    }

    @Override
    public void visit(VarEqualityExpr varEqualityExpr) {
        updateNewStateExpr(varEqualityExpr);
    }
}
