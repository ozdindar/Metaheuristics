package problems.mccdp.mutation;

import base.OptimizationProblem;
import exceptions.WrongIndividualType;
import metaheuristic.ea.base.MutationOperator;
import problems.mccdp.MCCDP;
import representation.IntegerPermutation;
import representation.IntegerVector;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 28.05.2015.
 */

/**
 *
 *
  */
public class RandomInsert implements MutationOperator {

    private static final int MAX_ATTEMPT = 5;

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        if (!(r instanceof IntegerVector))
            throw new WrongIndividualType("Remove can only be applied to IntegerVector");

        IntegerVector ni =(IntegerVector) r.clone();

        MCCDP mccdp = (MCCDP)problem;

        if ( mccdp.getWsnModel().noncoveredPointsCount(ni.getList())==0)
            return ni;

        int max = mccdp.targetPointCount();

        if (ni.getLength()>=max)
            return ni;

        int n = RandUtil.randInt(max);
        int attempt=0;
        while (ni.exists(n))
        {
            n = RandUtil.randInt(max);

            if (attempt++>MAX_ATTEMPT)
                return ni;
        }
        ni.getList().add(n);


        return (Representation)ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }


    public static void main(String args[])
    {
        IntegerPermutation ip = new IntegerPermutation(new int[]{1,2,3,4,5,6,7,8});
        RandomInsert m = new RandomInsert();

        System.out.println(ip);
        ip = (IntegerPermutation)m.apply(null,ip);
        System.out.println(ip);
    }
}
