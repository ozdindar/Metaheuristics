package metaheuristic.ea.terminalcondition;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.MetaHeuristic;
import representation.base.Population;

/**
 * Created by dindar.oz on 28.12.2016.
 */
public class NotImprovementTC implements TerminalCondition {

    private static final double IMPROVEMENT_THRESHOLD = 0.00001;

    double previousBestCost =0;
    int nonImprovementCounter;
    private int nonImprovementCredit;

    public NotImprovementTC(int nonImprovementCredit) {
        this.nonImprovementCredit = nonImprovementCredit;
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, Population population, OptimizationProblem problem) {

        return isSatisfied(alg,problem);
    }

    @Override
    public boolean isSatisfied(MetaHeuristic alg, OptimizationProblem problem) {
        if (Math.abs(alg.getBestKnownCost()-previousBestCost)< IMPROVEMENT_THRESHOLD)
            nonImprovementCounter++;
        else{
            nonImprovementCounter=0;
            previousBestCost = Math.abs(alg.getBestKnownCost());
        }
        return (nonImprovementCounter > nonImprovementCredit);
    }

    @Override
    public TerminalCondition clone() {
        return new NotImprovementTC(nonImprovementCredit);
    }

    @Override
    public void init() {
        nonImprovementCounter = 0;
    }
}
