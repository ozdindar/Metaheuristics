package metaheuristic.ea.mutation;

import base.OptimizationProblem;
import exceptions.WrongIndividualType;
import metaheuristic.ea.base.MutationOperator;
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
public class RandomRemove implements MutationOperator {

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        if (!(r instanceof IntegerVector))
            throw new WrongIndividualType("Remove can only be applied to IntegerVector");

        IntegerVector ni =(IntegerVector) r.clone();

        if (ni.getLength()<2)
            return ni;
        int index = RandUtil.randInt(ni.getLength());

        ni.getList().remove(index);
        //System.out.println("index: "+index);
        //System.out.println("nextIndex: " + nextIndex);

        return (Representation)ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }


    public static void main(String args[])
    {
        IntegerPermutation ip = new IntegerPermutation(new int[]{1,2,3,4,5,6,7,8});
        RandomRemove m = new RandomRemove();

        System.out.println(ip);
        ip = (IntegerPermutation)m.apply(null,ip);
        System.out.println(ip);
    }
}
