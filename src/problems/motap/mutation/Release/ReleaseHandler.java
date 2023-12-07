package problems.motap.mutation.Release;

import base.OptimizationProblem;
import representation.base.Individual;

public interface ReleaseHandler {

    boolean release(OptimizationProblem mp, Individual individual, int releaseCount);
    ReleaseHandler clone();
}
