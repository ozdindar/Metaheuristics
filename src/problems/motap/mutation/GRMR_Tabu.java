package problems.motap.mutation;

import base.NeighboringFunction;
import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Individual;
import util.ArrayUtil;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oz on 16.07.2015.
 */
public class GRMR_Tabu implements NeighboringFunction {

    int mutationCount=1;
    private int RelaxationCount=2;
    private int GreedyAttemptCount=3;
    private List<Integer> tabuList = new ArrayList<>();
    int tabuListSize =5;


    public GRMR_Tabu(int mutationCount, int relaxationCount, int greedyAttemptCount, int tabuListSize) {
        this.mutationCount = mutationCount;
        RelaxationCount = relaxationCount;
        GreedyAttemptCount = greedyAttemptCount;
        this.tabuListSize = tabuListSize;
    }

    public GRMR_Tabu() {

    }

    public GRMR_Tabu(int greedyAttemptCount, int tabuListSize) {
        this.GreedyAttemptCount = greedyAttemptCount;
        this.tabuListSize= tabuListSize;
    }

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {

        if (!(problem instanceof MOTAProblem))
            throw new InvalidProblem("Works only for MOTA Problems");

        if (!(i.getRepresentation() instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");

        MOTAProblem mp = (MOTAProblem)problem;
        Individual ni = i.clone();

        for (int mc=0;mc<mutationCount;mc++)
        {
            // todo: make this claculates cost difference
            mutateOneChromosome(mp,ni);
        }

        return ni;
    }

    @Override
    public NeighboringFunction clone() {
        return new GRMR_Tabu(mutationCount,RelaxationCount,GreedyAttemptCount,tabuListSize);
    }

    private void mutateOneChromosome(MOTAProblem mp, Individual ni) {
        IntegerAssignment assignment = (IntegerAssignment) ni.getRepresentation();
        boolean noAttempt = true;
        for (int m =0;m<GreedyAttemptCount;m++) {
            int chromosome = chooseChromosome(assignment.getLength());

            double gain = 0;
            int mutated = assignment.get(chromosome);

            for (int i = 0; i < mp.getDcs().getProcessors().size(); i++) {
                if (i == assignment.get(chromosome))
                    continue;

                double diff = mp.costDifference(mp.getTask(), chromosome, assignment.get(chromosome), i, assignment);
                if (diff < gain) {
                    mutated = i;
                    gain = diff;
                }
            }
            if (gain<0) {
                assignment.set(chromosome, mutated);
                noAttempt= false;
                ni.update(assignment,ni.getCost()+gain);
                addTabuList(chromosome);
            }
        }
        if (noAttempt)
        {
            // No gain from swap relax one of the constraints
            relax(mp,ni);
        }

    }

    private void addTabuList(int chromosome) {
        if (tabuList.size()== tabuListSize)
            tabuList.remove(0);
        tabuList.add(chromosome);
    }

    private int chooseChromosome(int length) {

        int r = RandUtil.randInt(length);

        while (tabuList.contains(new Integer(r)))
        {
            r = RandUtil.randInt(length);
        }
        return r;
    }



    private void relax(MOTAProblem mp, Individual ni) {
        for (int i=0;i<RelaxationCount;i++) {
            if(RandUtil.rollDice(0.5))
                relaxMemory(mp, ni);
            else relaxComputation(mp,ni);
        }
    }

    private void relaxMemory(MOTAProblem mp, Individual ni) {

        IntegerAssignment assignment = (IntegerAssignment) ni.getRepresentation();
        double[] memoryUsages = new double[mp.getDcs().getProcessors().size()];
        for (int i=0;i<mp.getDcs().getProcessors().size();i++)
        {
            memoryUsages[i] = (double)mp.getDcs().memoryUsageOf(i,mp.getTask(),assignment)/(double)mp.getDcs().getProcessors().get(i).getTotalMemory();
        }
        int mostUsedProcessor = ArrayUtil.getMaxIndex(memoryUsages);
        int leastUsedProcessor = ArrayUtil.getMinIndex(memoryUsages);

        List<Integer> usingModules = ArrayUtil.indexesOf(assignment.getValues(),mostUsedProcessor);


        int chromosome =  usingModules.get(RandUtil.randInt(usingModules.size()));
        double diff = mp.costDifference(mp.getTask(), chromosome, assignment.get(chromosome), leastUsedProcessor, assignment);
        assignment.set(chromosome,leastUsedProcessor);
        ni.update(assignment,ni.getCost()+diff);

    }

    private void relaxComputation(MOTAProblem mp, Individual ni) {
        IntegerAssignment assignment = (IntegerAssignment)ni.getRepresentation();
        double[] computationUsages = new double[mp.getDcs().getProcessors().size()];
        for (int i=0;i<mp.getDcs().getProcessors().size();i++)
        {
            computationUsages[i] = (double)mp.getDcs().computationUsageOf(i, mp.getTask(), assignment)/(double)mp.getDcs().getProcessors().get(i).getTotalComputationalResource();
        }
        int mostUsedProcessor = ArrayUtil.getMaxIndex(computationUsages);
        int leastUsedProcessor = ArrayUtil.getMinIndex(computationUsages);

        List<Integer> usingModules = ArrayUtil.indexesOf(assignment.getValues(),mostUsedProcessor);


        int chromosome =  usingModules.get(RandUtil.randInt(usingModules.size()));
        double diff = mp.costDifference(mp.getTask(), chromosome, assignment.get(chromosome), leastUsedProcessor, assignment);
        assignment.set(chromosome,leastUsedProcessor);
        ni.update(assignment,ni.getCost()+diff);


    }


}
