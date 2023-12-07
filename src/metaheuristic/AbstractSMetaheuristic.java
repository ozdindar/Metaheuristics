package metaheuristic;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.ea.terminalcondition.OrCompoundTC;
import problems.base.InitialSolutionGenerator;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;

import java.util.Arrays;

/**
 * Created by dindar.oz on 21.07.2016.
 */
public abstract class AbstractSMetaheuristic extends AbstractMetaheuristic {

    public Individual getCurrentSolution() {
        return currentSolution;
    }

    protected Individual currentSolution = null;

    public AbstractSMetaheuristic()
    {

    }

    public AbstractSMetaheuristic(AbstractSMetaheuristic sMetaheuristic) {
        super(sMetaheuristic);
        if (sMetaheuristic.currentSolution!= null)
            currentSolution = sMetaheuristic.currentSolution.clone();
    }


    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator)
    {
        init(problem);

        if (currentSolution == null)
            currentSolution = generateInitialSolution(problem,solutionGenerator);
        else updateBestIfNecessary(currentSolution.getRepresentation(),currentSolution.getCost());

        _perform(problem,solutionGenerator);
    }

    protected abstract void _perform(OptimizationProblem problem,InitialSolutionGenerator solutionGenerator);


    public void setCurrentSolution (Individual currentSolution)
    {
        this.currentSolution = currentSolution;
        neighboringCount=0;
    }

    private Individual generateInitialSolution(OptimizationProblem problem,InitialSolutionGenerator solutionGenerator) {
        Representation r = solutionGenerator.generate(problem,1).get(0);
        double cost = problem.cost(r);

        updateBestIfNecessary(r, cost);
        increaseNeighboringCount();

        return new SimpleIndividual(r, cost);
    }

    public abstract AbstractSMetaheuristic clone();

    public void addTerminalCondition(TerminalCondition terminalCondition) {
        if (this.terminalCondition instanceof OrCompoundTC)
        {
            OrCompoundTC tc = (OrCompoundTC) this.terminalCondition;
            tc.add(terminalCondition);
        }
        else this.terminalCondition = new OrCompoundTC(Arrays.asList(this.terminalCondition,terminalCondition));
    }
}
