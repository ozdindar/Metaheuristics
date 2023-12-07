package metaheuristic.ea.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import representation.base.Population;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class FitnessBasedTC implements TerminalCondition {
    double minFitness =0;

    public FitnessBasedTC(double minFitness) {
        this.minFitness = minFitness;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, Population population, OptimizationProblem problem) {
        return population.getBestCost()<=minFitness;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem)
    {
        return alg.getBestKnownCost()<=minFitness;
    }

    @Override
    public TerminalCondition clone() {
        return new FitnessBasedTC(minFitness);
    }

    @Override
    public void init() {

    }
}
