package metaheuristic.tabu.mediumtermmemory;

import base.OptimizationProblem;
import metaheuristic.tabu.MediumTermMemory;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Individual;
import representation.base.Representation;
import util.ArrayUtil;
import util.random.RandUtil;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by dindar.oz on 18.11.2016.
 */
public class MOTAPRecencyMatrix implements MediumTermMemory {


    private int forgetInterval=100;
    private double forgetRate= 0.2;

    int recencyMatrix[][];
    public int[][] moduleMaximums;

    int n;
    long updateCounter =0;


    public MOTAPRecencyMatrix(int n) {
        this.n = n;
    }

    public MOTAPRecencyMatrix(MOTAProblem problem, double ratio) {
        n = (int) (ratio*problem.getModuleCount());
    }

    public MOTAPRecencyMatrix(int n,int forgetInterval, double forgetRate) {
        this.forgetInterval = forgetInterval;
        this.forgetRate = forgetRate;
        this.n = n;
    }

    @Override
    public Representation generate(OptimizationProblem problem) {
        MOTAProblem motaProblem = (MOTAProblem) problem;

        int assignment[] = generateAssignment(motaProblem);

        return  new IntegerAssignment(assignment);
    }

    @Override
    public Representation diversify(OptimizationProblem problem, Representation representation) {

        return null;
    }


    private int[] generateAssignment(MOTAProblem motaProblem) {
        int assignment[] = new int[motaProblem.getModuleCount()];
        Arrays.fill(assignment,-1);
        int maxAssignments[][] = calculateMaxAssignments();
        for (int ma=0; ma<maxAssignments.length; ma++)
        {
            assignment[maxAssignments[ma][0]] = moduleMaximums[maxAssignments[ma][0]][1];
        }

        fillBlanks(assignment,motaProblem.getProcessorCount());
        return assignment;
    }


    private void fillBlanks(int assignment[],int processorCount) {
        for (int m= 0; m<assignment.length;m++ )
        {
            if (assignment[m]==-1) {
                assignment[m] = RandUtil.randInt(processorCount);
            }
        }
    }

    private int[][] calculateMaxAssignments() {
        int maxAssignments[][] = new int[n][2];

        int[][] cmoduleMaximums= new int[moduleMaximums.length][moduleMaximums[0].length] ;
        ArrayUtil.arrayCopy(moduleMaximums,cmoduleMaximums);

        Arrays.sort(cmoduleMaximums, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer time1 = entry1[2];
                final Integer time2 = entry2[2];
                return time2.compareTo(time1);
            }
        });

        for(int ma = 0; ma<maxAssignments.length;ma++)
        {
            maxAssignments[ma][1] = cmoduleMaximums[ma][1];
            maxAssignments[ma][0] = cmoduleMaximums[ma][0];
        }

        return maxAssignments;
    }

    @Override
    public void update(OptimizationProblem problem,Individual i, Individual best) {
        if (i==null)
            return;

        MOTAProblem motaProblem = (MOTAProblem)problem;
        IntegerAssignment assignment = (IntegerAssignment)i.getRepresentation();

        if ((updateCounter++)>0 && updateCounter%forgetInterval ==0)
            forget();
        if (recencyMatrix == null)
            return;

        for(int module =0; module<motaProblem.getModuleCount(); module++)
        {
            recencyMatrix[module][assignment.get(module)]++;
            if (moduleMaximums[module][2]<recencyMatrix[module][assignment.get(module)])
            {
                updateModuleMaximum(module,assignment.get(module));
            }
        }
    }

    private void forget() {
        for (int m =0; m<recencyMatrix.length;m++)
        {
            for (int p=0;p<recencyMatrix[0].length;p++)
            {
                recencyMatrix[m][p]=(int)(recencyMatrix[m][p]*(1-forgetRate));
                if (moduleMaximums[m][1]==p)
                    moduleMaximums[m][2]= recencyMatrix[m][p];
            }
        }
    }

    private void updateModuleMaximum(int module, int processor) {
        moduleMaximums[module][1] = processor;
        moduleMaximums[module][2] = recencyMatrix[module][processor];
    }

    @Override
    public void init(OptimizationProblem problem, boolean clearMemory) {
        updateCounter =0;
        MOTAProblem motaProblem = (MOTAProblem) problem;
        if (recencyMatrix == null || clearMemory)
        {
            recencyMatrix = new int[motaProblem.getModuleCount()][motaProblem.getProcessorCount()];
            moduleMaximums = new int[motaProblem.getModuleCount()][3];
            for (int m= 0; m<motaProblem.getModuleCount();m++)
                moduleMaximums[m][0] = m;
        }

    }

    @Override
    public boolean ready() {
        return true;
    }
}
