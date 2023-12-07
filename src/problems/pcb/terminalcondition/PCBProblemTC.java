package problems.pcb.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import problems.pcb.PCBProblem;
import representation.base.Population;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class PCBProblemTC implements TerminalCondition {
    long maxIteration =0;

    public PCBProblemTC(PCBProblem p ) {
        this.maxIteration = p.getN()*p.getN()*p.getN()/3;
    }

    public PCBProblemTC(long maxIteration) {
        this.maxIteration = maxIteration;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg,Population population, OptimizationProblem problem) {
        return (alg.getNeighboringCount()>maxIteration);
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        return (alg.getNeighboringCount()>maxIteration);
    }

    @Override
    public TerminalCondition clone() {
        return new PCBProblemTC(maxIteration);
    }

    @Override
    public void init() {

    }
}
