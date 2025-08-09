package metaheuristic.grasp;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.BaseSIterationEvent;
import problems.base.InitialSolutionGenerator;
import representation.SimpleIndividual;


public class Grasp extends AbstractSMetaheuristic {



    private int iterationCount=0;

    AbstractSMetaheuristic localSearch;

    public Grasp(AbstractSMetaheuristic localSearch, TerminalCondition terminalCondition) {
        this.localSearch = localSearch;
        this.terminalCondition = terminalCondition;
    }

    public Grasp(Grasp other) {
        super(other);
        localSearch = other.localSearch.clone();
        iterationCount = other.iterationCount;
    }


    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        while (!terminalCondition.isSatisfied(this,problem))
        {
            localSearch.perform(problem,solutionGenerator);
            currentSolution = new SimpleIndividual(localSearch.getBestKnownSolution(),localSearch.getBestKnownCost());
            updateBestIfNecessary(currentSolution.getRepresentation(),currentSolution.getCost());
            iterationCount++;
            System.out.println(iterationCount);
            increaseNeighboringCount((int) localSearch.getNeighboringCount());


            localSearch.setCurrentSolution(null);
            fireIterationEvent(new BaseSIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution,currentSolution.getCost(),currentSolution.getRepresentation()));
        }

    }

    @Override
    public AbstractSMetaheuristic clone() {
        AbstractSMetaheuristic clone = new Grasp(this);

        return clone;
    }


    @Override
    public String defaultName() {
        return "GRASP";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        iterationCount=0;
    }





}
