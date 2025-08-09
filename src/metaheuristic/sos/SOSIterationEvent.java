package metaheuristic.sos;

import metaheuristic.BaseSIterationEvent;
import representation.base.Representation;

/**
 * Created by dindar.oz on 22.06.2015.
 */
public class SOSIterationEvent extends BaseSIterationEvent {

    int childPopulationCount;

    public SOSIterationEvent(int iterationCount, long neighboringCount, double bestCost, Representation bestSolution,int childPopulationCount) {
        super(iterationCount,neighboringCount,bestCost,bestSolution,0.0,null);
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
