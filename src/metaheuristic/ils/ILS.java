package metaheuristic.ils;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.tabu.TabuIterationEvent;
import problems.base.InitialSolutionGenerator;
import representation.SimpleIndividual;

public class ILS extends AbstractSMetaheuristic {



    private int iterationCount=0;

    AbstractSMetaheuristic localSearch;
    Perturbator perturbator;



    public ILS(AbstractSMetaheuristic localSearch, Perturbator perturbator, TerminalCondition terminalCondition) {
        this.localSearch = localSearch;
        this.terminalCondition = terminalCondition;
        this.perturbator= perturbator;
    }

    public ILS(ILS other) {
        super(other);
        localSearch = other.localSearch.clone();
        iterationCount = other.iterationCount;
        perturbator = other.perturbator;
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

            perturbator.perturbate(problem,currentSolution);

            localSearch.setCurrentSolution(currentSolution);
            fireIterationEvent(new TabuIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
        }

    }

    @Override
    public AbstractSMetaheuristic clone() {
        AbstractSMetaheuristic clone = new ILS(this);

        return clone;
    }


    @Override
    public String defaultName() {
        return "ILS";
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
