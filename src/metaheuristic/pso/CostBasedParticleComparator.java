package metaheuristic.pso;

import metaheuristic.pso.base.PSOParticle;

import java.util.Comparator;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class CostBasedParticleComparator implements Comparator<PSOParticle> {
    @Override
    public int compare(PSOParticle o1, PSOParticle o2) {
        return Double.compare(o1.getCost(),o2.getCost());
    }
}
