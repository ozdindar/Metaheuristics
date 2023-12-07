package metaheuristic.ls;

import base.NeighboringFunction;
import base.OptimizationProblem;
import metaheuristic.AbstractSMetaheuristic;
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

    public LocalSearch(List<NeighboringFunction> nfList) {
        this.nfList = nfList;
    }

    @Override
    public int getIterationCount() {
        return 0;
    }

    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Individual currentState = currentSolution;
        NeighboringFunction nf = nfList.get(RandUtil.randInt(nfList.size()));

        while (true)
        {

            currentState = nf.apply(problem,currentState);

            if (currentState.getCost()>= bestKnownCost)
                return;
            updateBestIfNecessary(currentState.getRepresentation(),currentState.getCost());
        }
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
        return null;
    }
}
