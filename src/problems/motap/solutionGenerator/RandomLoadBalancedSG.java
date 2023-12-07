package problems.motap.solutionGenerator;

import base.OptimizationProblem;
import experiments.motap.MOTAPGenerator;
import problems.base.InitialSolutionGenerator;
import problems.motap.DCS;
import problems.motap.MOTAProblem;
import problems.motap.Task;
import representation.IntegerAssignment;
import representation.base.Representation;
import util.ArrayUtil;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by dindar.oz on 15.12.2016.
 */
public class RandomLoadBalancedSG implements InitialSolutionGenerator {

    boolean useMutationSize = false;

    public void setUseMutationSize(boolean useMutationSize) {
        this.useMutationSize = useMutationSize;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        List<Representation> states = new ArrayList<>();

        for (int i=0;i<c ;i++)
        {
            states.add(generateRandom((MOTAProblem) problem, rng));
        }

        return states;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem,RandUtil.getDefaultRNG(),c);
    }


    private Representation generateRandom(MOTAProblem problem, RNG rng) {
        int moduleCount = problem.getTask().getModules().size();
        int[] values = new int[ useMutationSize ? moduleCount+1:moduleCount] ;
        Vector<Integer> unassignedTasks = new Vector<>(problem.getTask().getModules().size());
        for (int i=0;i<values.length;i++)
        {
            values[i] = -1; // unassigned
            unassignedTasks.add(i);
        }
        for (int i=0;i<values.length;i++)
        {
            assignTask(problem,values,unassignedTasks);
        }

        if (useMutationSize)
            values[values.length-1]= mutationSize(problem,rng);// Test
        Representation state = new IntegerAssignment(values);
        return state;
    }

    private int mutationSize(MOTAProblem problem, RNG rng) {
        return -1*(2 + rng.randInt(problem.getModuleCount()-2));
    }

    private void assignTask(MOTAProblem mp,int[] values, Vector<Integer> unassignedTasks) {
        int unassignedTask = RandUtil.randInt(unassignedTasks.size());
        assignTask(mp,values, unassignedTasks.get(unassignedTask));
        unassignedTasks.remove(unassignedTask);
    }

    private void assignTask(MOTAProblem mp,int[] values, int unassignedTask) {

        double[] memoryUsages = new double[mp.getDcs().getProcessors().size()];
        double[] computationUsages = new double[mp.getDcs().getProcessors().size()];
        double[] totalUsage = new double[mp.getDcs().getProcessors().size()];
        IntegerAssignment assignment = new IntegerAssignment(values);
        for (int i=0;i<mp.getDcs().getProcessors().size();i++)
        {
            memoryUsages[i] = (double)mp.getDcs().memoryUsageOf(i,mp.getTask(),assignment)/(double)mp.getDcs().getProcessors().get(i).getTotalMemory();
            computationUsages[i] = (double)mp.getDcs().computationUsageOf(i, mp.getTask(), assignment)/(double)mp.getDcs().getProcessors().get(i).getTotalComputationalResource();
            totalUsage[i] = memoryUsages[i]*computationUsages[i];
        }
        int leastUsedProcessor = ArrayUtil.getMinIndex(totalUsage);
        values[unassignedTask] = leastUsedProcessor;

    }


    public static void main(String[] args) {
        DCS dcs= MOTAPGenerator.createRandomDCS(8);
        Task task = MOTAPGenerator.createRandomTask(80,8, 0.8);

        //DCS dcs= createSampleDCS();
        //Task task = createSampleTask();

        MOTAProblem p = new MOTAProblem(dcs,task);

        RandomLoadBalancedSG sg = new RandomLoadBalancedSG();
        List<Representation> representations = sg.generate(p,100);
        double avgDistance =0;
        long distance =0;
        for (int i1=0; i1<representations.size()-1; i1++)
        {
            for (int i2 = i1+1; i2<representations.size(); i2++)
            {
                distance += representations.get(i1).distanceTo(representations.get(i2));
            }
        }
        avgDistance = (double)2*distance/(double) (representations.size()*(representations.size()-1));
        System.out.println(avgDistance);
    }
}
