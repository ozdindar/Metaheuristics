package metaheuristic.dde;

import metaheuristic.BaseSIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 30.06.2015.
 */
public class DDEIterationEvent extends BaseSIterationEvent {

    public DDEIterationEvent(int iterationCount, long neighboringCount, double bestKnownCost, Representation bestKnownSolution, double currentCost, Representation currentSolution) {
        super(iterationCount,neighboringCount,bestKnownCost,bestKnownSolution, currentCost, currentSolution);
    }
}
