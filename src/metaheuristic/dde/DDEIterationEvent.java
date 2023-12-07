package metaheuristic.dde;

import metaheuristic.AbstractIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 30.06.2015.
 */
public class DDEIterationEvent extends AbstractIterationEvent {

    public DDEIterationEvent(int iterationCount, long neighboringCount, double bestKnownCost, Representation bestKnownSolution) {
        super(iterationCount,neighboringCount,bestKnownCost,bestKnownSolution);
    }
}
