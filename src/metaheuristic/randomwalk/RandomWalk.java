package metaheuristic.randomwalk;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.BaseSIterationEvent;
import problems.base.InitialSolutionGenerator;
import representation.SimpleIndividual;
import representation.base.Representation;

/**
 * Created by dindar.oz on 7/7/2017.
 */
public class RandomWalk extends AbstractSMetaheuristic {

    InitialSolutionGenerator isg;
    private int iterationCount;


    public RandomWalk(InitialSolutionGenerator isg, TerminalCondition tc) {
        this.isg = isg;
        this.terminalCondition = tc;
    }

    public RandomWalk(RandomWalk other) {
        this.isg = other.isg;
        iterationCount = other.iterationCount;
        terminalCondition= other.terminalCondition.clone();
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

    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {


        while (!terminalCondition.isSatisfied(this,problem))
        {
            Representation r = isg.generate(problem,1).get(0);
            currentSolution = new SimpleIndividual(r, problem.cost(r));
            updateBestIfNecessary(currentSolution.getRepresentation(),currentSolution.getCost());
            iterationCount++;
            fireIterationEvent(new BaseSIterationEvent(iterationCount,neighboringCount,bestKnownCost,bestKnownSolution,currentSolution.getCost(),currentSolution.getRepresentation()));

        }

        //printBest();
    }

    @Override
    public AbstractSMetaheuristic clone() {
        return new RandomWalk(this);
    }

    @Override
    public String defaultName() {
        return "Random-Walk";
    }


}
