package metaheuristic.sos;

import metaheuristic.AbstractIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 22.06.2015.
 */
public class SOSIterationEvent extends AbstractIterationEvent{

    int childPopulationCount;

    public SOSIterationEvent(int iterationCount, long neighboringCount, double bestCost, Representation bestSolution,int childPopulationCount) {
        super(iterationCount,neighboringCount,bestCost,bestSolution);
        this.childPopulationCount = childPopulationCount;
    }

    @Override
    public String toString() {
        return "SOSIterationEvent{" +
                "iterationCount=" + iterationCount +
                ", neighboringCount=" + neighboringCount +
                ", bestCost=" + bestCost +
                ", bestSolution=" + bestSolution +
                '}';
    }
}
