package metaheuristic.aco;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;

/**
 * Created by dindar.oz on 3.01.2017.
 */
public interface Colony {
    void init(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator);

    void makeTour(OptimizationProblem problem);

    Representation getBestSolution();

    double getBestCost();
}
