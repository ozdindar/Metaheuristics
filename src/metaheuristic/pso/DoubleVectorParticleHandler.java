package metaheuristic.pso;

import metaheuristic.pso.base.PSOProblem;
import metaheuristic.pso.base.ParticleHandler;
import metaheuristic.pso.base.Velocity;
import representation.DoubleVector;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 17.06.2015.
 */
public class DoubleVectorParticleHandler implements ParticleHandler {

    @Override
    public Class getRepresentationClass() {
        return DoubleVector.class;
    }

    @Override
    public Velocity generateInitialVelocity(PSOProblem problem, int length)
    {
        double initialVelocity = 0;
        double d = Math.abs(problem.getLowerBound()-problem.getUpperBound());

        DoubleVector v = new DoubleVector(length,0);


        for (int i=0;i<length;i++) {
            v.getValues()[i] = RandUtil.randDouble(-d, d);
        }

        return v;
    }

}
