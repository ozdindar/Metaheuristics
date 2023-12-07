package base;

import representation.base.Individual;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public interface NeighboringFunction {
    public Individual apply(OptimizationProblem problem, Individual i);
    NeighboringFunction clone();
}
