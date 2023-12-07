package problems.movingpeaks.crossover;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import metaheuristic.ea.base.CrossOverOperator;
import problems.movingpeaks.MovingPeaks;
import representation.DoubleVector;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 27.08.2015.
 */
public class SimpleMPCrossover implements CrossOverOperator {
    @Override
    public List<Representation> apply(OptimizationProblem problem, Representation p1, Representation p2) {
        if (!(problem instanceof MovingPeaks))
            throw new InvalidProblem("Only valid for MovingPeaks problem");

        if (!(p1 instanceof DoubleVector))
            throw new InvalidIndividual("Only valid for DoubleVector ");

        if (!(p2 instanceof DoubleVector))
            throw new InvalidIndividual("Only valid for DoubleVector ");

        MovingPeaks mp = (MovingPeaks) problem;
        DoubleVector dv1 = (DoubleVector)p1;
        DoubleVector dv2 = (DoubleVector)p2;

        DoubleVector dv3 = (DoubleVector) dv1.clone();
        DoubleVector dv4 = (DoubleVector) dv2.clone();

        int length = dv3.getValues().length;
        for (int i=0;i<length;i++)
        {
            if (i>length/2)
            {
                dv3.set(i,dv2.getValues()[i]);
                dv4.set(i,dv1.getValues()[i]);
            }
        }

        List<Representation> offsprings=  new ArrayList<>();
        offsprings.add(dv3);
        offsprings.add(dv4);
        return offsprings;
    }
}
