package metaheuristic.rbeamsearch;

import base.OptimizationProblem;
import representation.base.Individual;

import java.util.List;

public interface RBeamChildGenerator {
    void generate(OptimizationProblem problem, List<RBeamNode> children, List<Individual> parents);
    Individual createChild(OptimizationProblem problem, RBeamNode child, List<Individual> parents);
    RBeamChildGenerator clone();
}
