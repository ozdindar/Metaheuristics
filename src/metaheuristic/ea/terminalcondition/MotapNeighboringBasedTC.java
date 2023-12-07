package metaheuristic.ea.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import problems.motap.MOTAProblem;
import representation.base.Population;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class MotapNeighboringBasedTC implements TerminalCondition {
    long maxIterationCoeff =0;

    public MotapNeighboringBasedTC(long maxIterationCoeff) {
        this.maxIterationCoeff = maxIterationCoeff;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg,Population population, OptimizationProblem problem) {

        MOTAProblem motaProblem = (MOTAProblem) problem;

        long maxIteration = motaProblem.getModuleCount()*motaProblem.getProcessorCount()*maxIterationCoeff;
        return (alg.getNeighboringCount()> maxIteration);
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        MOTAProblem motaProblem = (MOTAProblem) problem;

        long maxIteration = motaProblem.getModuleCount()*motaProblem.getProcessorCount()*maxIterationCoeff;
        return (alg.getNeighboringCount()> maxIteration);
    }

    @Override
    public TerminalCondition clone() {
        return new MotapNeighboringBasedTC(maxIterationCoeff);
    }

    @Override
    public void init() {

    }
}
