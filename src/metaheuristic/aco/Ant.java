package metaheuristic.aco;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;

/**
 * Created by dindar.oz on 3.01.2017.
 */
public interface Ant {
    void init(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator);

    void leavePheromone(PheromoneTrail pheromoneTrail);

    boolean hasSolution();

    void proceed(OptimizationProblem problem);

    Representation getSolution();
}
