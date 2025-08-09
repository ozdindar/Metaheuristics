package metaheuristic.pso;

import metaheuristic.pso.base.PSOParticle;
import metaheuristic.pso.base.ContinousProblem;
import metaheuristic.pso.base.ParticleHandler;

import java.util.List;

public interface ExplorationEnhancer {
    void apply(ContinousProblem problem, int iteration, List<PSOParticle> swarm, ParticleHandler particleHandler);
}
