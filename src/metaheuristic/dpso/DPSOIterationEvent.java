package metaheuristic.dpso;

import metaheuristic.BaseSIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 25.06.2015.
 */
public class DPSOIterationEvent extends BaseSIterationEvent {
    public DPSOIterationEvent(int iterationCount, long neighboringCount, double bestKnownCost, Representation bestKnownSolution) {
        super(iterationCount,neighboringCount,bestKnownCost,bestKnownSolution,0.0,null);
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
