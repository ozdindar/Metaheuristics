package metaheuristic.tabu.base;

import base.NeighboringFunction;
import base.OptimizationProblem;
import representation.base.Representation;

/**
 * Created by dindar.oz on 23.06.2015.
 */
public interface TabuList {
    boolean isTabu(OptimizationProblem problem, Representation tmp, NeighboringFunction nf);

    void record(OptimizationProblem problem, Representation currentSolution, NeighboringFunction nf);
    void record(OptimizationProblem problem, Representation currentSolution);
}
