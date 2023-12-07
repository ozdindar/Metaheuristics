package metaheuristic.dpso;

import metaheuristic.AbstractIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 25.06.2015.
 */
public class DPSOIterationEvent extends AbstractIterationEvent {
    public DPSOIterationEvent(int iterationCount, long neighboringCount, double bestKnownCost, Representation bestKnownSolution) {
        super(iterationCount,neighboringCount,bestKnownCost,bestKnownSolution);
    }

    @Override
    public String toString() {
        return "PSOIterationEvent{" +
                "iterationCount=" + iterationCount +
                ", neighboringCount=" + neighboringCount +
                ", bestCost=" + bestCost +
                ", bestSolution=" + bestSolution +
                '}';
    }
}
