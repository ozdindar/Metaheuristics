package metaheuristic.aco.problem.tsp;

import base.OptimizationProblem;
import metaheuristic.aco.Ant;
import metaheuristic.aco.PheromoneTrail;
import problems.base.InitialSolutionGenerator;
import problems.tsp.TSPProblem;
import representation.IntegerPermutation;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 3.01.2017.
 */
public class TSPAnt implements Ant {
    List<Integer> currentPath = new ArrayList();
    int currentCity;
    Representation currentSolution;

    @Override
    public void init(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        TSPProblem tspProblem = (TSPProblem) problem;
        currentPath.clear();
        currentCity = RandUtil.randInt(tspProblem.getCityCount());
        currentPath.add(currentCity);
    }

    @Override
    public void leavePheromone(PheromoneTrail pheromoneTrail) {

    }

    @Override
    public boolean hasSolution() {
        return false;
    }

    @Override
    public void proceed(OptimizationProblem problem) {
        TSPProblem tspProblem = (TSPProblem) problem;
        if (currentPath.size()== tspProblem.getCityCount())
        {
            currentSolution = new IntegerPermutation(currentPath);
        }

    }

    @Override
    public Representation getSolution() {
        return currentSolution;
    }
}
