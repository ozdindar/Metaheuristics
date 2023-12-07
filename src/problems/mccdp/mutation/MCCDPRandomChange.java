package problems.mccdp.mutation;

import base.OptimizationProblem;
import exceptions.WrongIndividualType;
import metaheuristic.ea.base.MutationOperator;
import problems.mccdp.MCCDP;
import representation.base.Array;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class MCCDPRandomChange implements MutationOperator {
    private static final int MAX_ATTEMPT = 5;

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        if (!(r instanceof Array))
            throw new WrongIndividualType("Random Change can only be applied to permutation");

        MCCDP mccdp = (MCCDP)problem;

        Array p = (Array)r;


        int index = RandUtil.randInt(p.getLength());
        Array ni =(Array) r.clone();

        int newValue= RandUtil.randInt(mccdp.targetPointCount());

        int attempt =0;
        while (ni.exists(newValue)) {
            newValue = RandUtil.randInt(mccdp.targetPointCount());
            if (attempt++>MAX_ATTEMPT)
                return (Representation) ni;
        }

        ni.set(index,newValue);


        return (Representation)ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }
}
