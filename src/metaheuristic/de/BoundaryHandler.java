package metaheuristic.de;

import metaheuristic.pso.base.ContinousProblem;
import representation.DoubleVector;

public interface BoundaryHandler {
    void handle(ContinousProblem cProblem, double[] x, double[] target);
}
