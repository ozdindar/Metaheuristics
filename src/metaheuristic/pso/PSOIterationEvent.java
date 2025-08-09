package metaheuristic.pso;

import metaheuristic.BaseSIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 25.06.2015.
 */
public class PSOIterationEvent extends BaseSIterationEvent {
    public PSOIterationEvent(int iterationCount, long neighboringCount, double bestKnownCost, Representation bestKnownSolution) {
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
