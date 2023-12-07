package metaheuristic.ea.base;

import base.OptimizationProblem;
import representation.base.Individual;

import java.util.List;

/**
 * Created by dindar.oz on 11.06.2015.
 */
public interface CrossOverStrategy {
    List<Individual> generateOffsprings(OptimizationProblem problem,List<Individual> parents, List<CrossOverOperator> crossOverOperators);
}
