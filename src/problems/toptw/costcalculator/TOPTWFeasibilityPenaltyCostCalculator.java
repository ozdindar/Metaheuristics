package problems.toptw.costcalculator;

import base.OptimizationProblem;
import problems.toptw.CostCalculator;
import problems.toptw.TOPTWProblem;
import problems.toptw.representation.TourList;
import representation.base.Representation;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class TOPTWFeasibilityPenaltyCostCalculator implements CostCalculator {
    @Override
    public double calculateCost(OptimizationProblem problem, Representation i) {
        TOPTWProblem toptwProblem = (TOPTWProblem) problem;
        TourList tl = (TourList) i;

        double profit = toptwProblem.profit(tl);

        double penalty = toptwProblem.infeasibilityCount(tl)*toptwProblem.maxProfit();


        return -1*profit + penalty;
    }
}
