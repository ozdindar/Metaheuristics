package problems.motap.mutation;

import base.NeighboringFunction;
import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by oz on 16.07.2015.
 */
public class SimpleMOTAPNF implements NeighboringFunction {

    int mutationCount=0;

    public SimpleMOTAPNF(int mutationCount) {
        this.mutationCount = mutationCount;
    }

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {

        if (!(problem instanceof MOTAProblem))
            throw new InvalidProblem("Works only for MOTA Problems");


        Representation r = i.getRepresentation();


        if (!(r instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");

        MOTAProblem mp = (MOTAProblem)problem;
        IntegerAssignment nr = (IntegerAssignment)r.clone();


        double delta= 0;

        for (int mc=0;mc<mutationCount;mc++)
        {
            delta += mutateOneChromosome(mp,nr);
        }

        return new SimpleIndividual(nr,i.getCost()+delta);
    }


    public int generationCount(OptimizationProblem problem) {
        return 1;
    }

    @Override
    public NeighboringFunction clone() {
        NeighboringFunction clone = new SimpleMOTAPNF(mutationCount);
        return clone;
    }

    private double mutateOneChromosome(MOTAProblem mp, IntegerAssignment ni) {
        int chromosome = RandUtil.randInt(ni.getValues().length);

        int mutatedValue = RandUtil.randInt(mp.getDcs().getProcessors().size());


        double delta = mp.costDifference(mp.getTask(),chromosome,ni.get(chromosome),mutatedValue,ni);

        ni.getValues()[chromosome]= mutatedValue;

        return delta;
    }
}
