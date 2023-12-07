package metaheuristic.aco;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 3.01.2017.
 */
public class BasicAntColony implements Colony {

    PheromoneTrail pheromoneTrail;
    List<Ant> ants = new ArrayList();
    private Representation bestSolution;
    private double bestCost;

    @Override
    public void init(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        for (Ant ant:ants)
        {
            ant.init(problem,solutionGenerator);
        }
    }

    @Override
    public void makeTour(OptimizationProblem problem) {
        if (ants.isEmpty())
            return;
        while (!hasSolution()) {
            proceed(problem);
        }

        pheromoneTrail.evaporate();
        feedBack(problem);
    }

    private void feedBack(OptimizationProblem problem) {
        for (Ant ant : ants) {
            ant.leavePheromone(pheromoneTrail);
            updateBest(problem, ant);
        }
    }

    private void updateBest(OptimizationProblem problem, Ant ant) {
        double cost = problem.cost(ant.getSolution());
        if (cost<bestCost)
        {
            bestCost = cost;
            bestSolution = ant.getSolution();
        }
    }

    private void proceed(OptimizationProblem problem) {
        for (Ant ant : ants) {
            ant.proceed(problem);
        }
    }

    private boolean hasSolution() {
        return ants.get(0).hasSolution();
    }

    @Override
    public Representation getBestSolution() {
        return bestSolution;
    }

    @Override
    public double getBestCost() {
        return bestCost;
    }
}
