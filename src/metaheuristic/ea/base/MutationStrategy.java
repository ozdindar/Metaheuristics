package metaheuristic.ea.base;

import base.OptimizationProblem;
import representation.base.Population;

import java.util.List;

/**
 * Created by dindar.oz on 11.06.2015.
 */
public interface MutationStrategy {

    void applyMutations(OptimizationProblem problem, Population population, List<MutationOperator> mutationOperators);
    int getMutationCount();

}
