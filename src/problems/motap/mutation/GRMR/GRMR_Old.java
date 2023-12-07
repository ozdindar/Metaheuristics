package problems.motap.mutation.GRMR;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.motap.IProcessor;
import problems.motap.MOTAProblem;
import problems.motap.Pair;
import problems.motap.Processor;
import representation.IntegerAssignment;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GRMR_Old  implements MutationOperator {

    public static final int ROULETTE_SIZE = 10;

    Reassignment maximumReassignments[];

    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
        if (!(problem instanceof MOTAProblem))
            throw new InvalidProblem("Works only for MOTA Problems");

        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");

        IntegerAssignment assignment = (IntegerAssignment) i;
        MOTAProblem mp = (MOTAProblem) problem;

        if (!tryGreedyReassignment(mp,assignment))
            release(mp , assignment);

        return assignment;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private void release(MOTAProblem mp, IntegerAssignment assignment) {
        IProcessor fullProcessor = findFullestProcessor(mp , assignment);
        int index = mp.getDcs().getProcessors().indexOf(fullProcessor);
        int currentMemory = 0;
        int currentComputationalResource = 0;
        for(int j = 0 ; j<assignment.getLength() ; j++)
            if (assignment.get(j) == index) {
                currentMemory += mp.getTask().getModules().get(j).getMemoryRequirement();
                currentComputationalResource += mp.getTask().getModules().get(j).getCRR();
            }
        double memoryPercentage = (double) currentMemory/fullProcessor.getTotalMemory();
        double CRPercentage = (double) currentComputationalResource/fullProcessor.getTotalComputationalResource();
        if(CRPercentage>memoryPercentage)
            releaseCR(mp,index,assignment);
        else releaseMemory(mp,index,assignment);
    }

    private void releaseMemory(MOTAProblem mp, int index, IntegerAssignment assignment) {
        double[] weight = new double[assignment.getLength()];
        List<Pair<Integer , Integer>> taskIndexs = new ArrayList<>(); // For keeping track of original index of tasks
        int i = 0;
        for(int j = 0 ; j<assignment.getLength() ; j++){
            if(assignment.get(j)==index){
                weight[i] = mp.getTask().getModules().get(j).getMemoryRequirement();
                taskIndexs.add(new Pair(i,j)); // Made Pair Constructor Public
                i++;
            }
        }
        double[] roulette = Arrays.copyOfRange(weight,0,i);
        int k = RandUtil.rouletteSelect(roulette); //Selected a random task
        int taskIndex = 0;
        for(int a = 0 ; a<i ; a++) {
            if (taskIndexs.get(a).first == k) {
                taskIndex = taskIndexs.get(a).second;
                break;
            }
        }
        Reassignment reassignment = calcuklateMaximumReassignmentFor(mp,assignment,taskIndex);
        assignment.set(reassignment.taskId,reassignment.processor);
    }

    private void releaseCR(MOTAProblem mp, int index, IntegerAssignment assignment) {
        double[] weight = new double[assignment.getLength()];
        List<Pair<Integer , Integer>> taskIndexs = new ArrayList<>(); // For keeping track of original index of tasks
        int i = 0;
        for(int j = 0 ; j<assignment.getLength() ; j++){
            if(assignment.get(j)==index){
                weight[i] = mp.getTask().getModules().get(j).getCRR();
                taskIndexs.add(new Pair(i,j)); // Made Pair Constructor Public
                i++;
            }
        }
        double[] roulette = Arrays.copyOfRange(weight,0,i);
        int k = RandUtil.rouletteSelect(roulette); //Selected a random task
        int taskIndex = 0;
        for(int a = 0 ; a<i ; a++) {
            if (taskIndexs.get(a).first == k) {
                taskIndex = taskIndexs.get(a).second;
                break;
            }
        }
        Reassignment reassignment = calcuklateMaximumReassignmentFor(mp,assignment,taskIndex);
        assignment.set(reassignment.taskId,reassignment.processor);
    }

    private IProcessor findFullestProcessor(MOTAProblem mp, IntegerAssignment assignment) { // Which has highest percantage.
        List<IProcessor> pList = mp.getDcs().getProcessors();
        IProcessor fullestProcessor = null;
        double highestPercentage = 0;
        for(int i = 0 ; i<mp.getProcessorCount() ; i++){
            double percentage = getHighestPercentage(pList.get(i) , assignment ,mp, i);
            if(percentage>highestPercentage){
                fullestProcessor = pList.get(i);
                highestPercentage = percentage;
            }
        }
        return fullestProcessor;
    }

    private double getHighestPercentage(IProcessor processor, IntegerAssignment assignment, MOTAProblem mp, int i) {
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
            return CRPercentage;
        }
        else return memoryPercentage;
    }

    private boolean tryGreedyReassignment(MOTAProblem mp, IntegerAssignment assignment) {
        boolean improved = false;
        maximumReassignments = new Reassignment[assignment.getLength()];
        for (int t = 0; t < assignment.getLength(); t++) {
            maximumReassignments[t]  = calcuklateMaximumReassignmentFor(mp,assignment,t);
        }
        Arrays.sort(maximumReassignments,new DeltaCostBasedComparator());
        if(maximumReassignments[0].deltaCost >= 0){ // First element is the biggest , if it is less than or equal to 0 we have no gain.
            return false;
        }
        Reassignment reassignment = reassignmentWheel(maximumReassignments);
        assignment.set(reassignment.taskId,reassignment.processor);
        return true;
    }

    private Reassignment reassignmentWheel(Reassignment[] maximumReassignments) { //InverseRouletteWheel top 10 processors.
        double weight[] = new double[ROULETTE_SIZE];
        int i;
        for( i = 0 ; i< maximumReassignments.length && i<ROULETTE_SIZE ; i++){
            if(maximumReassignments[i].deltaCost>=0){ // Do not take negative or zero elements.
                break;
            }
            weight[i] = -1*maximumReassignments[i].deltaCost;
        }
        double roulette[] = Arrays.copyOfRange(weight,0,i);
        int index = RandUtil.rouletteSelect(roulette,i);
        return maximumReassignments[index];
    }

    private Reassignment calcuklateMaximumReassignmentFor(MOTAProblem mp, IntegerAssignment assignment, int t) {
        int originalIndex = assignment.get(t);
        Reassignment maximumReassigment = new Reassignment(t,originalIndex,Double.POSITIVE_INFINITY,originalIndex);
        for(int i = 0 ; i<mp.getProcessorCount() ; i++) {
            if (i == originalIndex)
                continue;

            double diff = mp.costDifference(mp.getTask(), t, originalIndex, i, assignment); // OriginalIndex's and i's positions reversed to get -costDifference.

            if(maximumReassigment.deltaCost>diff){
                maximumReassigment.deltaCost = diff;
                maximumReassigment.processor = i;
            }
        }

        return maximumReassigment;
    }

    public static void main(String args[]){
       /* Reassignment r1 = new Reassignment(1,2,12.155);
        Reassignment r2 = new Reassignment(1,2,17.15);
        Reassignment r3 = new Reassignment(1,2,10.15);
        Reassignment r4 = new Reassignment(1,2,-12.00);
        Reassignment r5 = new Reassignment(1,2,12.1523);
        Reassignment r6 = new Reassignment(1,2,12.15);   //DeltaCostBasedComparator Test , Compares by integer!!
        Reassignment rArray[] = {r1,r2,r3,r4,r5,r6};
        Arrays.sort(rArray,new DeltaCostBasedComparator());
        for(int i = 0 ; i<rArray.length ; i++){
            System.out.println(rArray[i].deltaCost);
        } */

        Processor p = new Processor(0.01 , 200 , 200 ,3);
        double x =(double) 1/2;
        System.out.println(x);

    }
}
