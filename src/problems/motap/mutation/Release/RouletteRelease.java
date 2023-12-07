package problems.motap.mutation.Release;

import base.OptimizationProblem;
import problems.motap.MOTAProblem;
import problems.motap.mutation.GRMR.Reassignment;
import representation.IntegerAssignment;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

public class RouletteRelease implements ReleaseHandler {
    @Override
    public boolean release(OptimizationProblem p, Individual individual, int releaseCount) {
        MOTAProblem mp = (MOTAProblem) p;
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        int size = mp.getDcs().getProcessors().size();
        for (int rc = 0; rc < releaseCount; rc++) {
            int selectedProcessor = selectProcessor(mp , assignment , size);

            releaseProcessor(mp,individual,selectedProcessor);
        }
        return true;
    }

    @Override
    public ReleaseHandler clone() {
        ReleaseHandler clone = new RouletteRelease();
        return clone;
    }

    private void releaseProcessor(MOTAProblem mp, Individual individual, int processor) {
        List<Reassignment> processorReassignments= new ArrayList<>();
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        for(int j = 0 ; j<assignment.getLength() ; j++){
            if(assignment.get(j)==processor){
                processorReassignments.add(calcuklateMaximumReassignmentFor(mp,assignment,j));
            }
        }
        double[] weight = new double[processorReassignments.size()];
        for(int i = 0 ; i<processorReassignments.size(); i++){
            weight[i] = processorReassignments.get(i).getDeltaCost();
        }
        int index = RandUtil.rouletteSelectInverse(weight);
        Reassignment reassignment = processorReassignments.get(index);
        assignment.set(reassignment.getTaskId(),reassignment.getProcessor());
        individual.update(assignment,individual.getCost()+ reassignment.getDeltaCost());

    }

    private int selectProcessor(MOTAProblem mp, IntegerAssignment assignment, int size) {
        double[] highestUsages = new double[size];
        for (int i=0;i<mp.getDcs().getProcessors().size();i++)
        {
            double memoryPercentage = (double)mp.getDcs().memoryUsageOf(i,mp.getTask(),assignment)/(double)mp.getDcs().getProcessors().get(i).getTotalMemory();
            double computationPercentage = (double)mp.getDcs().computationUsageOf(i, mp.getTask(), assignment)/(double)mp.getDcs().getProcessors().get(i).getTotalComputationalResource();
            if(computationPercentage>memoryPercentage){
                highestUsages[i] = computationPercentage;
            }
            else highestUsages[i] = memoryPercentage;
        }
        int index = RandUtil.rouletteSelect(highestUsages);
        return index;
    }

    private Reassignment calcuklateMaximumReassignmentFor(MOTAProblem mp, IntegerAssignment assignment, int t) {
        int originalIndex = assignment.get(t);
        Reassignment maximumReassigment = new Reassignment(t,originalIndex,Double.POSITIVE_INFINITY,originalIndex);
        for(int i = 0 ; i<mp.getProcessorCount() ; i++) {
            if (i == originalIndex)
                continue;

            double diff = mp.costDifference(mp.getTask(), t, originalIndex, i, assignment);


            if(maximumReassigment.getDeltaCost()>diff){
                maximumReassigment.setDeltaCost(diff);
                maximumReassigment.setProcessor(i);
            }
        }
        return maximumReassigment;
    }
}
