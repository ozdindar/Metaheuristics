package problems.nqueen;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.WrongIndividualType;
import metaheuristic.pso.base.PSOProblem;
import representation.*;
import representation.base.Representation;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class NQProblem implements OptimizationProblem,PSOProblem {

    int n;

    public NQProblem(int n) {
        this.n = n;
    }



    @Override
    public double cost(Representation r)  {

        if (!(r instanceof IntegerPermutation))
            throw new WrongIndividualType("NQ Individual must be Integer Array");
        IntegerPermutation ni = (IntegerPermutation)r;
        Integer queens[] = ni.getValues();

        if (queens.length != n)
            throw new InvalidIndividual("Not enough Queens");

        int conflict = 0;
        for (int q1 = 0; q1<n-1 ; q1++)
        {
            for (int q2 = q1+1; q2<n ; q2++ )
            {
                if (seeEachOther(q1,queens[q1],q2,queens[q2]) )
                    conflict++;
            }
        }

        return conflict;
    }

    @Override
    public double maxDistance() {
        return n;
    }

    @Override
    public boolean isFeasible(Representation r) {

        return (cost(r) == 0);
    }

    public static boolean seeEachOther(int q1x,int q1y,int q2x,int q2y)
    {
        if (q1x == q2x)
            return true;

        if (q1y == q2y)
            return true;

        if ( Math.abs(q1x-q2x) == Math.abs(q1y-q2y) )
            return true;

        return false;
    }


    @Override
    public int getDimensionCount() {
        return n;
    }

    @Override
    public double getUpperBound() {
        return n;
    }

    @Override
    public double getLowerBound() {
        return 0;
    }
}
