package problems.motap.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Representation;
import util.ArrayUtil;
import util.random.RandUtil;

import java.util.List;

/**
 * Created by oz on 16.07.2015.
 */
public class GreedySwapWithEscape implements MutationOperator {

    int mutationCount=2;
    private int RelaxationCount=3;
    private int GreedyAttemptCount=3;

    public GreedySwapWithEscape() {
    }

    public GreedySwapWithEscape(int greedyAttemptCount) {
        this.GreedyAttemptCount = greedyAttemptCount;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
/*
        if (!(problem instanceof MOTAProblem))
            throw new InvalidProblem("Works only for MOTA Problems");

        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");
*/
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
        return mutationCount+1;
    }

    private void mutateOneChromosome(MOTAProblem mp, IntegerAssignment ni) {
        boolean noAttempt = true;
        for (int m =0;m<GreedyAttemptCount;m++) {
            int chromosome = RandUtil.randInt(ni.getValues().length);

            double gain = 0;
            int mutated = ni.get(chromosome);

            for (int i = 0; i < mp.getDcs().getProcessors().size(); i++) {
                if (i == ni.get(chromosome))
                    continue;

                double diff = mp.costDifference(mp.getTask(), chromosome, ni.get(chromosome), i, ni);
                if (diff < gain) {
                    mutated = i;
                    gain = diff;
                }
            }
            if (gain<0) {
                ni.set(chromosome, mutated);
                noAttempt= false;
            }
        }
        if (noAttempt)
        {
            // No gain from swap relax one of the constraints
            relax(mp,ni);
        }

    }

    private void relax(MOTAProblem mp, IntegerAssignment ni) {
        for (int i=0;i<RelaxationCount;i++) {
            if(RandUtil.rollDice(0.5))
                relaxMemory(mp, ni);
            else relaxComputation(mp,ni);
        }
    }

    private void relaxMemory(MOTAProblem mp, IntegerAssignment ni) {
        double[] memoryUsages = new double[mp.getDcs().getProcessors().size()];
        for (int i=0;i<mp.getDcs().getProcessors().size();i++)
        {
            memoryUsages[i] = (double)mp.getDcs().memoryUsageOf(i,mp.getTask(),ni)/(double)mp.getDcs().getProcessors().get(i).getTotalMemory();
        }
        int mostUsedProcessor = ArrayUtil.getMaxIndex(memoryUsages);
        int leastUsedProcessor = ArrayUtil.getMinIndex(memoryUsages);

        List<Integer> usingModules = ArrayUtil.indexesOf(ni.getValues(),mostUsedProcessor);


        int chromosome =  usingModules.get(RandUtil.randInt(usingModules.size()));
        ni.set(chromosome,leastUsedProcessor);
    }

    private void relaxComputation(MOTAProblem mp, IntegerAssignment ni) {
        double[] computationUsages = new double[mp.getDcs().getProcessors().size()];
        for (int i=0;i<mp.getDcs().getProcessors().size();i++)
        {
            computationUsages[i] = (double)mp.getDcs().computationUsageOf(i, mp.getTask(), ni)/(double)mp.getDcs().getProcessors().get(i).getTotalComputationalResource();
        }
        int mostUsedProcessor = ArrayUtil.getMaxIndex(computationUsages);
        int leastUsedProcessor = ArrayUtil.getMinIndex(computationUsages);

        List<Integer> usingModules = ArrayUtil.indexesOf(ni.getValues(),mostUsedProcessor);


        int chromosome =  usingModules.get(RandUtil.randInt(usingModules.size()));
        ni.set(chromosome,leastUsedProcessor);
    }


}
