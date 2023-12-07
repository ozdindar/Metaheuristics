package metaheuristic.pso.base;

import base.OptimizationProblem;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public interface PSOProblem extends OptimizationProblem
{
    public int getDimensionCount();
    public double getUpperBound();
    public double getLowerBound();



}
