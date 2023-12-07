package metaheuristic.ea;

import metaheuristic.AbstractIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 22.06.2015.
 */
public class EAIterationEvent extends AbstractIterationEvent{


    public EAIterationEvent(long iterationCount,long neighboringCount, double bestCost, Representation bestSolution) {
        super(iterationCount,neighboringCount,bestCost,bestSolution);
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
