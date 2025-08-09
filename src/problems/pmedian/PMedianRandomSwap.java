package problems.pmedian;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import representation.BinaryString;
import representation.base.Representation;

import java.security.SecureRandom;
import java.util.Random;

public class PMedianRandomSwap implements MutationOperator {
    Random rnd = new SecureRandom();

    private final int swapCount;

    public PMedianRandomSwap(int swapCount)
    {
        this.swapCount = swapCount;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        assert r instanceof BinaryString;
        assert problem instanceof PMedianProblem;

        BinaryString bs = new BinaryString( r.toString());
        String code = bs.toString();

        PMedianProblem pMedianProblem = (PMedianProblem) problem;

        int p = pMedianProblem.getP();

        int[] facilities= new int[p];
        int f=0;
        for (int i = 0; i < bs.length(); i++) {
            if (bs.get(i).equals('1'))
                facilities[f++]=i;
        }



        for (int i = 0; i < swapCount; i++) {
            swapOne(bs, facilities);
        }

        return bs;
    }

    private void swapOne(BinaryString bs, int[] facilities) {
        int i = facilities[rnd.nextInt(facilities.length)];
        int j = rnd.nextInt(bs.length());

        bs.swap(i,j);
    }

}
