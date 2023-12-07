package metaheuristic.ea.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import representation.base.Population;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class IterationBasedTC implements TerminalCondition {
    long maxIteration =0;

    public IterationBasedTC(long maxIteration) {
        this.maxIteration = maxIteration;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg,Population population, OptimizationProblem problem) {
        return (alg.getIterationCount()>maxIteration);
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        return (alg.getIterationCount()>maxIteration);
    }

    @Override
    public TerminalCondition clone() {
        return new IterationBasedTC(maxIteration);
    }

    @Override
    public void init() {

    }
}
