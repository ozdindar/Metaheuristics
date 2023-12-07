package problems.motap.mutation.Release;

import base.OptimizationProblem;
import problems.motap.IProcessor;
import problems.motap.MOTAProblem;
import problems.motap.Pair;
import problems.motap.mutation.GRMR.Reassignment;
import problems.motap.mutation.GRMR.ResourceType;
import representation.IntegerAssignment;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

public class BasicRelease implements ReleaseHandler {
    public boolean release(OptimizationProblem p, Individual individual , int releaseCount) {
        MOTAProblem mp = (MOTAProblem) p;
        List<Integer> reassignedTasks = new ArrayList<>(releaseCount);
        //System.out.println("RELEASE");
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        for (int rc = 0; rc < releaseCount; rc++) {
            Pair<Integer, ResourceType> fullestProcessor = findFullestProcessor(mp , assignment);

            releaseProcessor(mp,individual,fullestProcessor.first,reassignedTasks);
        }
        return true;
    }

    @Override
    public ReleaseHandler clone() {
        ReleaseHandler clone = new BasicRelease();
        return clone;
    }

    private void releaseProcessor(MOTAProblem mp, Individual individual, Integer processor, List<Integer> reassignedTasks) {
        List<Reassignment> processorReassignments= new ArrayList<>();
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        for(int j = 0 ; j<assignment.getLength() ; j++){
            if(assignment.get(j)==processor && !reassignedTasks.contains(j)){
                processorReassignments.add(calcuklateMaximumReassignmentFor(mp,assignment,j)); // This could slow us down.2
            }
        }
        if (processorReassignments.isEmpty())
            return;
        double[] weight = new double[processorReassignments.size()];
        for(int i = 0 ; i<processorReassignments.size(); i++){ // Top 10 or All
            weight[i] = processorReassignments.get(i).getDeltaCost();
        }
        int index = RandUtil.rouletteSelectInverse(weight);
        Reassignment reassignment = processorReassignments.get(index);
        assignment.set(reassignment.getTaskId(),reassignment.getProcessor());
        individual.update(assignment,individual.getCost()+ reassignment.getDeltaCost());
        reassignedTasks.add(reassignment.getTaskId());
    }

    private Pair<Integer, ResourceType> findFullestProcessor(MOTAProblem mp, IntegerAssignment assignment) { // Which has highest percantage.
        List<IProcessor> pList = mp.getDcs().getProcessors();
        int fullestProcessor=0;
        double highestPercentage = 0;
        ResourceType highestResource= ResourceType.Memory;
        for(int i = 0 ; i<mp.getProcessorCount() ; i++){
            Pair<ResourceType,Double> percentage = getHighestPercentage(pList.get(i) , assignment ,mp, i);
            if(percentage.second.doubleValue() >highestPercentage){
                fullestProcessor = i;
                highestPercentage = percentage.second.doubleValue();
                highestResource = percentage.first;
            }
        }
        return new Pair<>(fullestProcessor,highestResource);
    }

    private Pair<ResourceType,Double> getHighestPercentage(IProcessor processor, IntegerAssignment assignment, MOTAProblem mp, int i) {
        int currentMemory = 0;
        int currentComputationalResource = 0;
        for(int j = 0 ; j<assignment.getLength() ; j++){
            if(assignment.get(j) == i){
                currentMemory += mp.getTask().getModules().get(j).getMemoryRequirement();
                currentComputationalResource += mp.getTask().getModules().get(j).getCRR();
            }
        }
        double memoryPercentage = (double) currentMemory/processor.getTotalMemory();
        double CRPercentage = (double) currentComputationalResource/processor.getTotalComputationalResource();
        if(CRPercentage>memoryPercentage){
            return new Pair<>(ResourceType.Computation,CRPercentage);
        }
        else return new Pair<>(ResourceType.Memory,memoryPercentage);
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
