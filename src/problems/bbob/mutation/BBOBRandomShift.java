package problems.bbob.mutation;

import base.NeighboringFunction;
import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.bbob.BBOBProblem;
import representation.DoubleVector;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;

import java.security.SecureRandom;
import java.util.Random;

public class BBOBRandomShift implements MutationOperator, NeighboringFunction {
    private static final double MAX_CHANGE = 0.1;

    double maxChange;
    Random rng = new SecureRandom();

    public BBOBRandomShift() {
        maxChange = MAX_CHANGE;
    }

    public BBOBRandomShift(double maxChange) {
        this.maxChange = maxChange;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        BBOBProblem bp = (BBOBProblem) problem;
        DoubleVector dv = (DoubleVector) r.clone();
        double[] values = dv.getValues();
        int i = rng.nextInt( dv.size());
        double delta = rng.nextDouble()*maxChange;
        if (rng.nextBoolean()) delta *=-1;
        values[i] = Math.min(Math.max(values[i]+delta, bp.lowerBound(i) ), bp.upperBound(i));
        return dv;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {
        Representation r= apply(problem,i.getRepresentation());
        return new SimpleIndividual(r,problem.cost(r));
    }

    @Override
    public NeighboringFunction clone() {
        return new BBOBRandomShift(maxChange);
    }
}
