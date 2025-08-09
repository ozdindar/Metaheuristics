package metaheuristic.tabu;

import metaheuristic.BaseSIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 23.06.2015.
 */
public class TabuIterationEvent extends BaseSIterationEvent {

    public TabuIterationEvent(long iterationCount,long neighboringCount,double bestCost, Representation bestSolution, double currentCost, Representation currentSolution) {
        super(iterationCount,neighboringCount,bestCost,bestSolution, currentCost,currentSolution);
    }

    @Override
    public String toString() {
        return "TabuIterationEvent{" +
                "iterationCount=" + iterationCount +
                ", neighboringCount=" + neighboringCount +
                ", bestCost=" + bestCost +
                ", bestSolution=" + bestSolution +
                '}';
    }
}
