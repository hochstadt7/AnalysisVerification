package ast;

import java.util.*;

public class AssertVerifyVisitor {
    // Parity stuff
    Map<String, String> currParity;
    Map<String, Map<String, String>> relationalParity;
    Map<String, Map<String, Integer>> countSameParity;
    Map<String, Map<String, Integer>> countDiffParity;
    // CP stuff
    Map<String, Integer> currCP;
    // VE stuff
    Set<VariableEquality> currVE;

    public AssertVerifyVisitor(
            Map<String, String> currParity,
            Map<String, Map<String, String>> relationalState,
            Map<String, Integer> currCP,
            Set<VariableEquality> currVE
    ) {
        this.currParity = currParity;
        this.relationalParity = relationalState;
        this.countSameParity = initCombination();
        this.countDiffParity = initCombination();
        this.currCP = currCP;
        this.currVE = currVE;
    }

    private Map<String, Map<String, Integer>> initCombination(){
        Map<String, Map<String, Integer>> bottoms = new HashMap<>();
        for (String var : currParity.keySet()) {
            Map<String,Integer> internal = new HashMap<>();
            for(String var2: currParity.keySet()) {
                if(!var.equals(var2)) {
                    internal.put(var2, 0);
                }
            }
            bottoms.put(var, internal);
        }
        return bottoms;
    }


    private void incrementPredicateCounter(Map<String, Map<String, Integer>> countRelation, String var1, String var2) {
        int count = countRelation.get(var1).get(var2);
        countRelation.get(var1).put(var2, count + 1);
        countRelation.get(var2).put(var1, count + 1); // same update- symmetric
    }

    private void checkRelationalParity(List<Predicate> andClause) {
        int countOddPred = 0;
        String var1 = andClause.get(0).getId();
        String var2 = andClause.get(1).getId();

        for (Predicate pred : andClause) {
            if (pred instanceof OddPred) {
                countOddPred++;
            }
        }
        if (countOddPred % 2 == 0) { // same parity
            // update counter
            incrementPredicateCounter(countSameParity, var1, var2);
        } else if (countOddPred % 2 == 1) { // not same parity
            incrementPredicateCounter(countDiffParity, var1, var2);
        }
    }


    public boolean visit(AssertCmd assertCmd) {
        List<List<Predicate>> dnf = assertCmd.getDNF();
        // for dnf to hold, we need one and clause that holds
        boolean anyTrue = false;
        for (List<Predicate> andClause : dnf) {
            boolean anyFalse = false;
            int numOfPredicates = andClause.size();
            if (numOfPredicates == 2) {
                checkRelationalParity(andClause);
            }

            for (Predicate pred : andClause) {
                if (!pred.acceptVerifier(this)) {
                    anyFalse = true;
                }
            }
            if (!anyFalse) { // and clause holds
                anyTrue = true;
                break;
            }
        }
        if (anyTrue) {
            return true;
        }
        for (String var : currParity.keySet()){
            for (String var2 : currParity.keySet()){
                if (!var.equals(var2)) {
                    if (countSameParity.get(var).get(var2) == 2) { // (EVEN var EVEN var2) (ODD var ODD var2)
                        if (relationalParity.get(var).get(var2).equals(ParityVisitor.EVEN))
                            return true;
                    }
                    if (countDiffParity.get(var).get(var2) == 2) { // (EVEN var ODD var2) (ODD var EVEN var2)
                        if (relationalParity.get(var).get(var2).equals(ParityVisitor.ODD))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean visit(EvenPred evenPred) {
        String parity = currParity.get(evenPred.getId());
        return parity.equals(ParityVisitor.EVEN);
    }

    public boolean visit(OddPred oddPred) {
        String parity = currParity.get(oddPred.getId());
        return parity.equals(ParityVisitor.ODD);
    }

    private boolean hasNumericalValue(String var) {
        Integer CPAbsVal = currCP.get(var);
        return !(CPAbsVal.equals(CPVisitor.TOP) || CPAbsVal.equals(CPVisitor.BOTTOM));
    }

    private boolean allNumerical(List<String> varList) {
        for (String term : varList) {
            if (!hasNumericalValue(term)) {
                return false;
            }
        }
        return true;
    }

    private Integer calculateVarSum(List<String> termList) {
        Integer sum = 0;
        for (String term : termList) {
            sum += currCP.get(term);
        }
        return sum;
    }

    private boolean areSumsEqual(List<String> leftTerms, List<String> rightTerms) {

        Integer leftSum = calculateVarSum(leftTerms);
        Integer rightSum = calculateVarSum(rightTerms);
        return leftSum.equals(rightSum);
    }

    public boolean visit(SumPred sumPred) {
        List<String> leftTerms = sumPred.getLeftTerms();
        List<String> rightTerms = sumPred.getRightTerms();
        List<String> allTerms = new ArrayList<>(leftTerms);
        allTerms.addAll(rightTerms);

        if (allNumerical(allTerms)) {
            return areSumsEqual(leftTerms, rightTerms);
        }
        List<String> leftCopy = new ArrayList<>(leftTerms);
        List<String> rightCopy = new ArrayList<>(rightTerms);
        for (String var1 : leftTerms) {
            for (String var2 : rightTerms) {
                boolean sameVal = rightCopy.contains(var2) && // verify we haven't used var2 already
                        (currVE.contains(new VariableEquality(var1, var2)) || var1.equals(var2));
                if (sameVal) {
                    leftCopy.remove(var1);
                    rightCopy.remove(var2);
                    break;
                }
            }
        }
        if (leftCopy.isEmpty() && rightCopy.isEmpty()) {
            return true;
        }
        allTerms.clear();
        allTerms.addAll(leftCopy);
        allTerms.addAll(rightCopy);
        if (allNumerical(allTerms)) {
            return areSumsEqual(leftTerms, rightTerms);
        }
        return false;
    }
}