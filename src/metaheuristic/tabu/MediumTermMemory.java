package metaheuristic.tabu;

import base.OptimizationProblem;
import representation.base.Individual;
import representation.base.Representation;

/**
 * Created by dindar.oz on 18.11.2016.
 */
public interface MediumTermMemory {
    Representation generate(OptimizationProblem problem);

    Representation diversify(OptimizationProblem problem, Representation representation);

    void update(OptimizationProblem problem, Individual i, Individual best);


    void init(OptimizationProblem problem, boolean clearMemory);

    boolean ready();
}

