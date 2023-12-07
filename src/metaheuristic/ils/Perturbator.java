package metaheuristic.ils;

import base.OptimizationProblem;
import representation.base.Individual;

public interface Perturbator {

    void perturbate(OptimizationProblem problem, Individual individual);
    Perturbator clone();
}
