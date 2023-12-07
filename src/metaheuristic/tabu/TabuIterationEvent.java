package metaheuristic.tabu;

import metaheuristic.AbstractIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 23.06.2015.
 */
public class TabuIterationEvent extends AbstractIterationEvent{

    public TabuIterationEvent(long iterationCount,long neighboringCount,double bestCost, Representation bestSolution) {
        super(iterationCount,neighboringCount,bestCost,bestSolution);
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
