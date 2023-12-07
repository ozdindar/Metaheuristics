package metaheuristic.ea.base;

import base.OptimizationProblem;
import representation.base.Representation;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public interface MutationOperator {
    public Representation apply(OptimizationProblem problem, Representation i);
    int neighboringCount();
}
