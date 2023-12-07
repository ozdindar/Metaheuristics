package metaheuristic;

import representation.base.Representation;

/**
 * Created by dindar.oz on 25.06.2015.
 */
public abstract class AbstractIterationEvent implements IterationEvent {
    protected final long iterationCount;
    protected final double bestCost;
    protected final Representation bestSolution;
    protected final long neighboringCount;

    public AbstractIterationEvent(long iterationCount,long neighboringCount, double bestCost, Representation bestSolution) {
        this.iterationCount = iterationCount;
        this.bestCost = bestCost;
        this.bestSolution  =bestSolution;
        this.neighboringCount = neighboringCount;
    }

    @Override
    public long getIterationCount() {
        return iterationCount;
    }

    @Override
    public long getNeighboringCount() {
        return neighboringCount;
    }

    @Override
    public double getBestCost() {
        return bestCost;
    }

    @Override
    public Representation getBestSolution() {
        return bestSolution;
    }
}
