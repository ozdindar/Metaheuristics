package problems.motap.mutation.Release;

import base.OptimizationProblem;
import problems.motap.MOTAProblem;
import problems.motap.mutation.GRMR.Reassignment;
import representation.IntegerAssignment;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

public class ReleaseWithMemory implements ReleaseHandler {
    ArrayList<Integer> memory = new ArrayList<>();
    //List<Individual> individualList = new ArrayList<>();

    @Override
    public boolean release(OptimizationProblem p, Individual individual, int releaseCount) {
        // TEST //
       // if(individualList.contains(individual)){
       //     System.out.println("Same Release");
       // }
       // individualList.add(individual.clone());
        // TEST // */
        MOTAProblem mp = (MOTAProblem) p;
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        ArrayList<Integer> newMemory = new ArrayList<>();
        int size = mp.getDcs().getProcessors().size();
        boolean released = false;
        for (int rc = 0; rc < releaseCount; rc++) {
            int selectedProcessor = selectProcessor(mp , assignment , size);
            if (selectedProcessor==-1)
                return released;
            released |= releaseProcessor(mp,individual,selectedProcessor,newMemory);
        }
        memory = newMemory;
        return released;
    }

    @Override
    public ReleaseHandler clone() {
        ReleaseHandler clone = new ReleaseWithMemory();
        return clone;
    }

    private boolean releaseProcessor(MOTAProblem mp, Individual individual, int processor, ArrayList<Integer> processorMemory) {
        List<Reassignment> processorReassignments= new ArrayList<>();
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        for(int task = 0 ; task<assignment.getLength() ; task++){
            if(assignment.get(task)==processor){
                Reassignment reassignment = calcuklateMaximumReassignmentFor(mp,assignment,task,processorMemory);
                if (reassignment != null)
                    processorReassignments.add(reassignment);
            }
        }
        int index=0;
        if (processorReassignments.isEmpty())
            return false;
        else if (processorReassignments.size()>1)
        {
            double[] weight = new double[processorReassignments.size()];
            for (int i = 0; i < processorReassignments.size(); i++) {
                weight[i] = processorReassignments.get(i).getDeltaCost();
            }
            index = RandUtil.rouletteSelectInverse(weight);
        }
        Reassignment reassignment = processorReassignments.get(index);
        assignment.set(reassignment.getTaskId(),reassignment.getProcessor());
        individual.update(assignment,individual.getCost()+ reassignment.getDeltaCost());
        processorMemory.add(reassignment.getOldProcessor());
        return true;
    }

    private int selectProcessor(MOTAProblem mp, IntegerAssignment assignment, int size) {
        double[] highestUsages = new double[size];
        boolean processorAdded= false;
        for (int p=0;p<mp.getDcs().getProcessors().size();p++)
        {
            if (!memory.contains(p)) {
                double memoryPercentage = (double) mp.getDcs().memoryUsageOf(p, mp.getTask(), assignment) / (double) mp.getDcs().getProcessors().get(p).getTotalMemory();
                double computationPercentage = (double) mp.getDcs().computationUsageOf(p, mp.getTask(), assignment) / (double) mp.getDcs().getProcessors().get(p).getTotalComputationalResource();
                if (computationPercentage > memoryPercentage) {
                    highestUsages[p] = computationPercentage;
                } else highestUsages[p] = memoryPercentage;
                processorAdded = true;
            }
        }
        return (processorAdded)? RandUtil.rouletteSelect(highestUsages):-1;


    }

    private Reassignment calcuklateMaximumReassignmentFor(MOTAProblem mp, IntegerAssignment assignment, int task, ArrayList<Integer> processorMemory) {
        int originalIndex = assignment.get(task);
        Reassignment maximumReassigment = null;
        for(int i = 0 ; i<mp.getProcessorCount() ; i++) {
            if (i == originalIndex || processorMemory.contains(i))
                continue;

            double diff = mp.costDifference(mp.getTask(), task, originalIndex, i, assignment);


            if( maximumReassigment== null || maximumReassigment.getDeltaCost()>diff){
                maximumReassigment = new Reassignment(task,i,diff,originalIndex);
            }
        }
        return maximumReassigment;
    }

}
