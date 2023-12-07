package metaheuristic.pso.base;

/**
 * Created by dindar.oz on 17.06.2015.
 */
public interface ParticleHandler {

    Class getRepresentationClass();
    Velocity generateInitialVelocity(PSOProblem problem, int dimensionCount);
}
