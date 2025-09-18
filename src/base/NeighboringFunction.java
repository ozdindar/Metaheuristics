package base;

import representation.base.Individual;

import java.util.Collections;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public interface NeighboringFunction {
    Individual apply(OptimizationProblem problem, Individual i);
    NeighboringFunction clone();

    default List<Individual> applyAll(OptimizationProblem problem, Individual i)
    {
        return Collections.singletonList(apply(problem,i));
    }
}
