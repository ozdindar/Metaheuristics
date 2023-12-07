package problems.motap.mutation.GRMR;

import base.NeighboringFunction;
import base.OptimizationProblem;
import problems.motap.IProcessor;
import problems.motap.MOTAProblem;
import problems.motap.Pair;
import representation.IntegerAssignment;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GRMRNF implements NeighboringFunction {

    public static final int ROULETTE_SIZE = 10;

    //Reassignment maximumReassignments[];
    private int releaseCount=1;

    public GRMRNF(int releaseCount) {
        this.releaseCount = releaseCount;
    }

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {

        Individual individual = i.clone();
        MOTAProblem mp = (MOTAProblem) problem;
        if(individual.getCost()<0){
            boolean aha = true;
        }

        Reassignment[] maximumReassignments = new Reassignment[mp.getModuleCount()];

        if (!tryGreedyReassignment(mp,individual,maximumReassignments))
            release(mp ,individual);

        return individual;
    }

    @Override
    public NeighboringFunction clone() {
        return new GRMRNF(releaseCount);
    }

    private void release(MOTAProblem mp, Individual individual) {
        //System.out.println("RELEASE");
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        for (int rc = 0; rc < releaseCount; rc++) {
            Pair<Integer, ResourceType> fullestProcessor = findFullestProcessor(mp , assignment);

            releaseProcessor(mp,individual,fullestProcessor.first);
        }

        /* todo: we should review this. Seems not correct here
        if(fullestProcessor.second== ResourceType.Computation)
            releaseCR(mp,fullestProcessor.first,assignment);
        else releaseMemory(mp,fullestProcessor.first,assignment);
        */
    }

    private void releaseProcessor(MOTAProblem mp, Individual individual, Integer processor) {
        List<Reassignment> processorReassignments= new ArrayList<>();
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        for(int j = 0 ; j<assignment.getLength() ; j++){
            if(assignment.get(j)==processor){
                processorReassignments.add(calcuklateMaximumReassignmentFor(mp,assignment,j));
            }
        }
        double[] weight = new double[processorReassignments.size()];
        for(int i = 0 ; i<processorReassignments.size(); i++){ // Top 10 or All
            weight[i] = processorReassignments.get(i).deltaCost;
        }
        int index = RandUtil.rouletteSelectInverse(weight);
        Reassignment reassignment = processorReassignments.get(index);
        assignment.set(reassignment.taskId,reassignment.processor);
        individual.update(assignment,individual.getCost()+ reassignment.getDeltaCost());
        if(individual.getCost()<0){
            boolean aha = true;
        }
        /*todo: inverse roulette wheel ; update assignment*/

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

    private boolean tryGreedyReassignment(MOTAProblem mp, Individual individual , Reassignment[] maximumReassignments) {
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();
        for (int t = 0; t < assignment.getLength(); t++) {
            maximumReassignments[t]  = calcuklateMaximumReassignmentFor(mp,assignment,t);
        }
        Arrays.sort(maximumReassignments,new DeltaCostBasedComparator());
        if(maximumReassignments[0].deltaCost >= 0){ // First element is the biggest , if it is less than or equal to 0 we have no gain.
            return false;
        }
        Reassignment reassignment = reassignmentWheel(maximumReassignments);
        assignment.set(reassignment.taskId,reassignment.processor);
        if(individual.getCost()+reassignment.getDeltaCost()<0){
            boolean aha = true;
        }
        individual.update(assignment,individual.getCost()+ reassignment.getDeltaCost());
        if(individual.getCost()<0){
            boolean aha = true;
        }
        return true;
    }

    private Reassignment reassignmentWheel(Reassignment[] maximumReassignments) { //InverseRouletteWheel top 10 processors.
        double weight[] = new double[ROULETTE_SIZE];
        int i;
        for( i = 0 ; i<ROULETTE_SIZE ; i++){
            if(maximumReassignments[i].deltaCost>=0){ // Do not take negative or zero elements.
                break;
            }
            weight[i] = -1*maximumReassignments[i].deltaCost;
        }

        int index = RandUtil.rouletteSelect(weight,i);
        return maximumReassignments[index];
    }

    private Reassignment reassignmentInverseWheel(Reassignment[] reassignments) { //InverseRouletteWheel top 10 processors.
        double weight[] = new double[ROULETTE_SIZE];
        int i;
        for( i = 0 ; i<ROULETTE_SIZE ; i++){
            weight[i] = reassignments[i].deltaCost;
        }

        int index = RandUtil.rouletteSelectInverse(weight);
        return reassignments[index];
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

    }
}
