package problems.dejong;

import exceptions.InvalidIndividual;
import metaheuristic.pso.base.PSOProblem;
import representation.DoubleVector;
import representation.base.Representation;

/**
 * Created by dindar.oz on 28.12.2015.
 */
public class DejongF1 implements PSOProblem {

    public static final double MAX_X = 5.12;
    public static final double MIN_X = -5.12;

    int n;

    public DejongF1(int n) {
        this.n = n;
    }


    @Override
    public boolean isFeasible(Representation i) {
        if (! (i instanceof DoubleVector))
            throw new InvalidIndividual("DejongF1 takes Double Vector");

        DoubleVector dv = (DoubleVector)i;


        if (dv.size()!= n)
            throw new InvalidIndividual("vector size is invalid");


        for (int j=0;j<dv.size();j++)
        {
            if ( dv.getValues()[j]>MAX_X || dv.getValues()[j]<MIN_X)
                return false;
        }

        return true;
    }

    @Override
    public double cost(Representation i) {
        double sum =0;

        DoubleVector dv = (DoubleVector)i;

        for (int j=0;j<dv.size();j++)
        {
            sum += dv.getValues()[j]*dv.getValues()[j];
        }
        return sum;
    }

    @Override
    public double maxDistance() {
        return n*2*MAX_X;
    }

    @Override
    public int getDimensionCount() {
        return n;
    }

    @Override
    public double getUpperBound() {
        return MAX_X;
    }

    @Override
    public double getLowerBound() {
        return MIN_X;
    }
}
