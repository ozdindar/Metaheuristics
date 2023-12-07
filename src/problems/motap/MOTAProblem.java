package problems.motap;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import experiments.motap.MOTAPGenerator;
import problems.base.InitialSolutionGenerator;
import representation.IntegerAssignment;
import representation.base.Representation;
import util.random.RandUtil;

import java.io.*;

/**
 * Created by dindar.oz on 15.07.2015.
 */
public class MOTAProblem implements OptimizationProblem,Serializable{

    DCS dcs;
    Task task;

    public void setSolutionGenerator(InitialSolutionGenerator solutionGenerator) {
        this.solutionGenerator = solutionGenerator;
    }

    InitialSolutionGenerator solutionGenerator ;

    public double ReliabilityFactor= 1;
    public double CostFactor= 1;

    public MOTAProblem(DCS dcs, Task task) {
        this.dcs = dcs;
        this.task = task;
    }

    public DCS getDcs() {
        return dcs;
    }

    public Task getTask() {
        return task;
    }

    public double getReliabilityFactor() {
        return ReliabilityFactor;
    }

    @Override
    public boolean isFeasible(Representation i) {
        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("MOTAP accepts only IntegerAssignment");



        return false;
    }

    @Override
    public double cost(Representation i) {
        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("MOTAP accepts only IntegerAssignment");

        IntegerAssignment allocation = (IntegerAssignment) i;

        double systemCost = dcs.systemCostOf(task,allocation);
        double reliabilityCost = (ReliabilityFactor==0)? 1:dcs.reliabilityCostOf(task,allocation);
        double infeasibility = dcs.infeasibilityFactorOf(task,allocation);

        double cost = CostFactor*systemCost +  ReliabilityFactor*reliabilityCost + infeasibility;

        return cost;
    }

    @Override
    public double maxDistance() {
        return 0;
    }


    public double reliabilityCost(Representation i) {
        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("MOTAP accepts only IntegerAssignment");

        IntegerAssignment allocation = (IntegerAssignment) i;

        double reliabilityCost = (ReliabilityFactor==0)? 1:dcs.reliabilityCostOf(task,allocation);

        return ReliabilityFactor*reliabilityCost;
    }

    public double systemCost(Representation i) {
        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("MOTAP accepts only IntegerAssignment");

        IntegerAssignment allocation = (IntegerAssignment) i;

        double systemCost = dcs.systemCostOf(task,allocation);

        return CostFactor*systemCost;
    }

    public double infeasibilityCost(Representation i) {
        if (!(i instanceof IntegerAssignment))
            throw new InvalidIndividual("MOTAP accepts only IntegerAssignment");

        IntegerAssignment allocation = (IntegerAssignment) i;

        double infeasibility = dcs.infeasibilityFactorOf(task,allocation);

        return infeasibility;
    }

    public void writeToFile(String fileName) throws IOException {
        FileOutputStream fout = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(this);
    }

    public static MOTAProblem readFromFile(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream streamIn = new FileInputStream(fileName);
        ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);

        MOTAProblem p = (MOTAProblem) objectinputstream.readObject();
        return p;
    }

    public double costDifference(Task task, int m, int p1, int p2, IntegerAssignment allocation )
    {
        if (p1==p2)
            return 0.0;

        double diff= dcs.costDifference(task,m,p1,p2,allocation,CostFactor,ReliabilityFactor);

        return diff;
    }

    public int getModuleCount() {

        return task.getModuleCount();
    }

    public int getProcessorCount() {
        return dcs.getProcessorCount();
    }

    public static void main(String[] args) {
        MOTAProblem problem = MOTAPGenerator.createRandomProblem(5,4,0.8,0.5);

        DCS dcs= MOTAPGenerator.createRandomDCS(8);
        Task task = MOTAPGenerator.createRandomTask(5,8, 0.8);

        MOTAProblem problem2 = new MOTAProblem(dcs,task);
        IntegerAssignment assignment = new IntegerAssignment(new int[]{0,1,1,2,0});
        double c = problem.cost(assignment);
        for (int i = 0; i <100 ; i++) {
            IntegerAssignment newAssignment = (IntegerAssignment) assignment.clone();
            int module = RandUtil.randInt(problem.getModuleCount());
            int processor = RandUtil.randInt(problem.getProcessorCount());

            double delta = problem.costDifference(problem.getTask(),module,newAssignment.get(module),processor,newAssignment);

            newAssignment.set(module,processor);

            double c1 = problem.cost(newAssignment);
            if (Math.abs(c1-c-delta)>0.0001)
            {
                System.out.println("DELTA:" +Math.abs(c1-c-delta));
            }
            else System.out.println("EQUAL");
        }
    }
}
