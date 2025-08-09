package problems.bbob.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ils.Perturbator;
import problems.bbob.BBOBProblem;
import representation.DoubleVector;
import representation.base.Individual;
import representation.base.Representation;

import java.security.SecureRandom;
import java.util.Random;

public class BBOBPerturbator implements Perturbator {
    private static final double MAX_CHANGE = 3.0;
    private static final double DEFAULT_CHANGE_RATE = 0.5;

    double changeRate;
    double maxChange;
    Random rng = new SecureRandom();

    public BBOBPerturbator() {
        changeRate = DEFAULT_CHANGE_RATE;
        maxChange = MAX_CHANGE;
    }

    public BBOBPerturbator(double changeRate,double maxChange) {
        this.changeRate= changeRate;
        this.maxChange = maxChange;
    }




    @Override
    public void perturbate(OptimizationProblem problem, Individual individual) {
        BBOBProblem bp = (BBOBProblem) problem;
        int changeCount = (int) (changeRate*bp.getDimension());
        DoubleVector dv = (DoubleVector) individual.getRepresentation();

        for (int c = 0; c < changeCount; c++) {
            int i = rng.nextInt( dv.size());
            double v = dv.getValues()[i];
            double delta = rng.nextDouble()*maxChange;
            if (rng.nextBoolean()) delta *=-1;
            double nv = Math.min(Math.max(v+delta, bp.lowerBound(i) ), bp.upperBound(i));

            dv.set(i,nv);
        }

        individual.update(dv,problem.cost(dv));
    }

    @Override
    public Perturbator clone() {
        return new BBOBPerturbator(changeRate,maxChange);
    }
}
