package metaheuristic.pso.base;

import base.OptimizationProblem;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public interface ContinousProblem extends OptimizationProblem
{
    public int getDimensionCount();
    public double getUpperBound(int d);
    public double getLowerBound(int d);



}
