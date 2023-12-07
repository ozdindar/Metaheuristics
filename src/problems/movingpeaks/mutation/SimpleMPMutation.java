package problems.movingpeaks.mutation;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.movingpeaks.MovingPeaks;
import representation.DoubleVector;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 27.08.2015.
 */
public class SimpleMPMutation implements MutationOperator{

    double magnitude =0;

    public SimpleMPMutation(double magnitude) {
        this.magnitude = magnitude;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
        if (!(problem instanceof MovingPeaks))
            throw new InvalidProblem("Only valid for MovingPeaks problem");

        if (!(i instanceof DoubleVector))
            throw new InvalidIndividual("Only valid for DoubleVector ");

        MovingPeaks mp = (MovingPeaks) problem;
        DoubleVector dv = (DoubleVector)i;

        DoubleVector mutated = (DoubleVector) dv.clone();

        int  c = RandUtil.randInt(dv.getValues().length);
        double v= dv.getValues()[c];
        double maxChange = (mp.getUpperBound()-mp.getLowerBound())*magnitude;
        double change = RandUtil.randDouble(maxChange/2,maxChange);
        if (RandUtil.rollDice(0.5))
            change *=-1;

        mutated.set(c,v+change);

        return mutated;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }
}
