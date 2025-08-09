package metaheuristic.ls;

import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.BaseSIterationEvent;
import problems.base.InitialSolutionGenerator;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 7/7/2017.
 */
public class LocalSearch extends AbstractSMetaheuristic {

    List<NeighboringFunction> nfList;
    int iterationCount;

    public LocalSearch(List<NeighboringFunction> nfList) {
        this.nfList = nfList;
        terminalCondition = TerminalCondition.NullTC;
    }

    public LocalSearch(List<NeighboringFunction> nfList, TerminalCondition tc) {
        this.nfList = nfList;
        this.terminalCondition = tc;
    }


    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Individual currentState = currentSolution;
        NeighboringFunction nf = nfList.get(RandUtil.randInt(nfList.size()));

        while (!terminalCondition.isSatisfied(this,problem))
        {
            iterationCount++;
            currentState = nf.apply(problem,currentState);

            if (currentState.getCost()>= bestKnownCost)
                return;
            updateBestIfNecessary(currentState.getRepresentation(),currentState.getCost());

            fireIterationEvent(new BaseSIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution, currentSolution.getCost(), currentSolution.getRepresentation()));

        }
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        iterationCount=0;
    }

    @Override
    public AbstractSMetaheuristic clone() {
        List<NeighboringFunction> nfList = new ArrayList<>();

        for (NeighboringFunction nf:this.nfList)
            nfList.add(nf.clone());
        return new LocalSearch(nfList);
    }

    @Override
    public String defaultName() {
        return "LocalSearch";
    }
}
