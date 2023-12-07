package metaheuristic.dpso;

import metaheuristic.dpso.base.DPSOParticle;

import java.util.Comparator;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class CostBasedParticleComparator implements Comparator<DPSOParticle> {
    @Override
    public int compare(DPSOParticle o1, DPSOParticle o2) {
        return Double.compare(o1.getCost(),o2.getCost());
    }
}
