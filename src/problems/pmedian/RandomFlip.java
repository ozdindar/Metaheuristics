package problems.pmedian;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import representation.BinaryString;
import representation.base.Representation;

import java.security.SecureRandom;
import java.util.Random;

public class RandomFlip implements MutationOperator {
    private final int flipCount;

    public RandomFlip(int flipCount)
    {
        this.flipCount = flipCount;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        assert r instanceof BinaryString;
        Random rnd = new SecureRandom();
        BinaryString bs = new BinaryString( r.toString());

        for (int i = 0; i < flipCount; i++) {
            int ndx= rnd.nextInt(bs.length());
            bs.flip(ndx);
        }

        return bs;
    }

}
