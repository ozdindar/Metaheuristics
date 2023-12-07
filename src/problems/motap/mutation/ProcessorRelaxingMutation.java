package problems.motap.mutation;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
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
public class ProcessorRelaxingMutation implements MutationOperator {

    double greed = 0;
    int mutationCount=0;


    public ProcessorRelaxingMutation(int mutationCount, double greed) {
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

        initMemoryUsages(mp,ni);

        for (int mc=0;mc<mutationCount;mc++)
        {
            if (RandUtil.rollDice(greed))
                mutateSmart(mp, ni);
            else mutateRandom(mp,ni);
        }

        return ni;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private void initMemoryUsages(MOTAProblem mp, IntegerAssignment ni) {
        memoryUsages = new double[mp.getDcs().getProcessors().size()];
        for (int i=0;i<mp.getDcs().getProcessors().size();i++)
        {
            memoryUsages[i] = (double)mp.getDcs().memoryUsageOf(i,mp.getTask(),ni)/(double)mp.getDcs().getProcessors().get(i).getTotalMemory();
        }
    }

    double memoryUsages[] = null;

    private void mutateSmart(MOTAProblem mp, IntegerAssignment ni) {

        int mostUsedProcessor = ArrayUtil.getMaxIndex(memoryUsages);
        int leastUsedProcessor = ArrayUtil.getMinIndex(memoryUsages);

        List<Integer> usingModules = ArrayUtil.indexesOf(ni.getValues(),mostUsedProcessor);


        int chromosome =  usingModules.get(RandUtil.randInt(usingModules.size()));

        int oldProcessor =  ni.getValues()[chromosome];
        ni.getValues()[chromosome]= leastUsedProcessor;
        updateMemoryUsage(oldProcessor,mp,ni);
        updateMemoryUsage(leastUsedProcessor,mp,ni);

    }

    private void updateMemoryUsage(int processor, MOTAProblem mp,IntegerAssignment allocation)
    {
        if (processor >= memoryUsages.length)
            return;
        memoryUsages[processor] =  (double)mp.getDcs().memoryUsageOf(processor,mp.getTask(),allocation)/(double)mp.getDcs().getProcessors().get(processor).getTotalMemory();
    }

    private void mutateRandom(MOTAProblem mp, IntegerAssignment ni) {
        int chromosome = RandUtil.randInt(ni.getValues().length);

        int mutatedValue = RandUtil.randInt(mp.getDcs().getProcessors().size());

        int oldProcessor = ni.get(chromosome);
        ni.getValues()[chromosome]= mutatedValue;
        updateMemoryUsage(oldProcessor,mp,ni);
        updateMemoryUsage(mutatedValue,mp,ni);

    }


}
