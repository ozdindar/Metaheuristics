package metrics;

import base.OptimizationProblem;
import metaheuristic.MetaHeuristicListener;
import metrics.partitioning.SearchSpacePartitioner;

public interface DataBuilder<T> extends MetaHeuristicListener {
    T getData();
    void init(OptimizationProblem problem, SearchSpacePartitioner ssp);
}
