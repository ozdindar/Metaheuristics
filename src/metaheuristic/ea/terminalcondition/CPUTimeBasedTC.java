package metaheuristic.ea.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import representation.base.Population;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class CPUTimeBasedTC implements TerminalCondition {
    long maxCPUTime =0;
    long startTime=0;

    public CPUTimeBasedTC(long maxCPUTime) {
        this.maxCPUTime = maxCPUTime;
    }

    public CPUTimeBasedTC(long maxCPUTime, long startTime) {
        this.maxCPUTime = maxCPUTime;
        this.startTime = startTime;
    }

    @Override
    public synchronized boolean isSatisfied(MetaHeuristic alg,Population population, OptimizationProblem problem) {
        return (System.currentTimeMillis()-startTime> maxCPUTime);
    }

    @Override
    public synchronized boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        return (System.currentTimeMillis()-startTime> maxCPUTime);
    }

    @Override
    public TerminalCondition clone() {
        return new CPUTimeBasedTC(maxCPUTime,startTime);
    }

    @Override
    public void init() {
        startTime = System.currentTimeMillis();
    }
}
