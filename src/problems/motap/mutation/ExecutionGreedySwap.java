package problems.motap.mutation;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.motap.IModule;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by oz on 16.07.2015.
 */
public class ExecutionGreedySwap implements MutationOperator {

    double greed = 0;
    int mutationCount=0;


    public ExecutionGreedySwap(int mutationCount, double greed) {
        this.mutationCount = mutationCount;
        this.greed = greed;
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

        IModule m = mp.getTask().getModules().get(chromosome);
        int[] aets = m.getAETs();

        int mutatedValue = RandUtil.rollDice(greed) ? getMinIndex(aets) : RandUtil.randInt(mp.getDcs().getProcessors().size());

        int processor= ni.getValues()[mutatedValue];
        boolean enoughMemory = (  mp.getDcs().getProcessors().get(processor).getTotalMemory()-mp.getDcs().memoryUsageOf(processor,mp.getTask(),ni) > mp.getTask().getModules().get(chromosome).getMemoryRequirement() );
        boolean enoughComputation = (  mp.getDcs().getProcessors().get(processor).getTotalComputationalResource()-mp.getDcs().computationUsageOf(processor,mp.getTask(),ni) > mp.getTask().getModules().get(chromosome).getCRR() );

        if ( enoughComputation && enoughMemory  )
            ni.getValues()[chromosome]= mutatedValue;
    }

    private int getMinIndex(int[] aets) {
        int minVal = aets[0];
        int minIndex = 0;
        for (int i=1;i<aets.length;i++)
        {
            if (aets[i]<minVal)
            {
                minVal = aets[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
}
