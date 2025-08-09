package problems.toptw.costcalculator;

import base.OptimizationProblem;
import problems.toptw.CostCalculator;
import problems.toptw.TOPTWProblem;
import problems.toptw.representation.Tour;
import problems.toptw.representation.TourList;
import representation.base.Representation;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class TOPTWProfitOnlyCostCalculator implements CostCalculator {
    @Override
    public double calculateCost(OptimizationProblem problem, Representation i) {
        TOPTWProblem toptwProblem = (TOPTWProblem) problem;
        TourList pl = (TourList) i;

        double profit =0;
        for (Tour tour:pl.tours)
        {
            profit += toptwProblem.profit(tour);
        }

        return -1*profit;
    }
}
