package metaheuristic.ea;

import metaheuristic.BaseSIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 22.06.2015.
 */
public class EAIterationEvent extends BaseSIterationEvent {


    public EAIterationEvent(long iterationCount,long neighboringCount, double bestCost, Representation bestSolution, double currentCost, Representation currentSolution) {
        super(iterationCount,neighboringCount,bestCost,bestSolution,currentCost, currentSolution);
    }

    @Override
    public String toString() {
        return "EAIterationEvent{" +
                "iterationCount=" + iterationCount +
                ", neighboringCount=" + neighboringCount +
                ", bestCost=" + bestCost +
                ", bestSolution=" + bestSolution +
                '}';
    }
}
