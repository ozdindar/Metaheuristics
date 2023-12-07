package metaheuristic.rbeamsearch;

import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractSMetaheuristic;
import problems.base.InitialSolutionGenerator;


public class SimpleLocalSearch extends AbstractSMetaheuristic {

    NeighboringFunction neighboringFunction;
    private int iterationCount=0;
    TerminalCondition tc;

    public SimpleLocalSearch(NeighboringFunction neighboringFunction, TerminalCondition tc) {
        this.neighboringFunction = neighboringFunction;
        this.tc = tc;
    }

    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        while (!tc.isSatisfied(this,problem)) {
            currentSolution = neighboringFunction.apply(problem, currentSolution);


            updateBestIfNecessary(currentSolution.getRepresentation(), currentSolution.getCost());
            iterationCount = 1;
            increaseNeighboringCount();
        }
    }

    @Override
    public AbstractSMetaheuristic clone() {
        AbstractSMetaheuristic clone = new SimpleLocalSearch(neighboringFunction.clone(),tc.clone());
        return clone;
    }

    @Override
    public String defaultName() {
        return "SLS";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }
}
