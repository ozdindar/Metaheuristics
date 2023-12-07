package metaheuristic.island;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.base.Individual;

import java.util.ArrayList;

public interface IslandModul {
    void init(OptimizationProblem problem);

    void runFor(int migrationPeriod, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator);

    Individual getBestSolution();

    ArrayList<Individual> getImmigrants(int immigrantCount);
    void acceptImmigrants(ArrayList<Individual> immigrants);

    long getNeighboringCount();
}
