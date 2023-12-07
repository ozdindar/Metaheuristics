package problems.toptw;

import base.OptimizationProblem;
import representation.base.Representation;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public interface CostCalculator {

    public double calculateCost(OptimizationProblem problem, Representation i);
}
