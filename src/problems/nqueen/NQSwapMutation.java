package problems.nqueen;

import base.OptimizationProblem;
import exceptions.WrongIndividualType;
import metaheuristic.ea.base.MutationOperator;
import representation.IntegerPermutation;
import representation.base.Array;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dindar.oz on 23.06.2015.
 */
public class NQSwapMutation implements MutationOperator {
    @Override
    public Representation apply(OptimizationProblem problem, Representation r)
    {
        if (!(r instanceof Array))
            throw new WrongIndividualType("Swap can only be applied to permutation");

        if (!(problem instanceof NQProblem))
            throw new WrongIndividualType("NQSwap can only be applied to NQProblem");


        IntegerPermutation ip = (IntegerPermutation)r;

        NQProblem nqp = (NQProblem)problem;


        Set<Integer> conflictingSet = new HashSet<>();

        for (int q1 = 0; q1<nqp.n-1 ; q1++)
        {
            for (int q2 = q1+1; q2<nqp.n ; q2++ )
            {
                if (NQProblem.seeEachOther(q1, ip.get(q1), q2, ip.get(q2)) ) {
                    conflictingSet.add(q1);
                    conflictingSet.add(q2);

                }
            }
        }

        int n1 = RandUtil.randInt(nqp.n);
        int n2 = RandUtil.randInt(nqp.n);

        while (n2 == n1 || (!conflictingSet.contains(n1)&& !conflictingSet.contains(n2)))
            n2 = RandUtil.randInt(nqp.n);

        IntegerPermutation ni = (IntegerPermutation) ip.clone();

        ni.swap(n1,n2);

        return ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }
}
