package problems.motap.mutation;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by oz on 16.07.2015.
 */
public class SimpleMOTAPMutation implements MutationOperator {

    int mutationCount=0;

    public SimpleMOTAPMutation(int mutationCount) {
        this.mutationCount = mutationCount;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {

        if (!(problem instanceof MOTAProblem))
            throw new InvalidProblem("Works only for MOTA Problems");

        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");

        MOTAProblem mp = (MOTAProblem)problem;
        IntegerAssignment ni = (IntegerAssignment)i.clone();

        for (int mc=0;mc<mutationCount;mc++)
        {
            mutateOneChromosome(mp,ni);
        }

        return ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private void mutateOneChromosome(MOTAProblem mp, IntegerAssignment ni) {
        int chromosome = RandUtil.randInt(ni.getValues().length);

        int mutatedValue = RandUtil.randInt(mp.getDcs().getProcessors().size());

        ni.getValues()[chromosome]= mutatedValue;
    }
}
