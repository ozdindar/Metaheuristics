package base;

import metaheuristic.MetaHeuristic;
import representation.base.Population;

/**
 * Created by dindar.oz on 22.04.2015.
 */
public interface TerminalCondition {
    boolean isSatisfied(MetaHeuristic alg,Population population, OptimizationProblem problem);
    boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem);
    TerminalCondition clone();
    void init();
}
