package problems.base;

import base.OptimizationProblem;
import representation.base.Representation;
import util.random.RNG;

import java.util.List;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public interface InitialSolutionGenerator {
    List<Representation> generate(OptimizationProblem problem, RNG rng, int c);
    List<Representation> generate(OptimizationProblem problem, int c);
}
