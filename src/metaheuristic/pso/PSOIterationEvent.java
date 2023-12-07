package metaheuristic.pso;

import metaheuristic.AbstractIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 25.06.2015.
 */
public class PSOIterationEvent extends AbstractIterationEvent {
    public PSOIterationEvent(int iterationCount, long neighboringCount, double bestKnownCost, Representation bestKnownSolution) {
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
