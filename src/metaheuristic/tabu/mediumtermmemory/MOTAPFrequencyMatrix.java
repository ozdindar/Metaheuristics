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
public class MOTAPFrequencyMatrix implements MediumTermMemory {


    private static final double READY_THRESHOLD = 0.1;
    int frequencyMatrix[][];
    int moduleMinimums[][];

    int nonZeroEntryCount =0;

    int n;

    public MOTAPFrequencyMatrix(int n) {
        this.n = n;
    }

    public MOTAPFrequencyMatrix(MOTAProblem problem, double ratio) {
        n = (int) (ratio*problem.getModuleCount());
    }

    @Override
    public Representation generate(OptimizationProblem problem) {
        MOTAProblem motaProblem = (MOTAProblem) problem;

        int assignment[] = generateAssignment(motaProblem);

        return  new IntegerAssignment(assignment);
    }

    @Override
    public Representation diversify(OptimizationProblem problem, Representation representation) {
        MOTAProblem motaProblem = (MOTAProblem) problem;
        IntegerAssignment assignment = (IntegerAssignment) representation;

        int newAssignment[] = diversifyAssignment(motaProblem,assignment.getValues());
        return new IntegerAssignment(newAssignment);
    }


    private int[] generateAssignment(MOTAProblem motaProblem) {
        int assignment[] = new int[motaProblem.getModuleCount()];
        Arrays.fill(assignment,-1);
        if( ready())
        {
            int minAssignments[][] = calculateMinAssignments();
            for (int ma = 0; ma < minAssignments.length; ma++) {
                assignment[minAssignments[ma][0]] = moduleMinimums[minAssignments[ma][0]][1];
            }
        }
        fillBlanks(assignment,motaProblem.getProcessorCount());
        return assignment;
    }

    private int[] diversifyAssignment(MOTAProblem motaProblem, int[] assignment) {
        int newAssignment[] = new int[motaProblem.getModuleCount()];
        Arrays.fill(newAssignment,-1);
        if( ready())
        {
            int minAssignments[][] = calculateMinAssignments();
            for (int ma = 0; ma < minAssignments.length; ma++) {
                newAssignment[minAssignments[ma][0]] = moduleMinimums[minAssignments[ma][0]][1];
            }
        }
        fillBlanks(newAssignment,assignment);
        return newAssignment;
    }

    private void    fillBlanks(int[] assignment, int[] oldAssignment) {
        for (int m= 0; m<assignment.length;m++ )
        {
            if (assignment[m]==-1) {
                assignment[m] = oldAssignment[m];
            }
        }
    }

    private void fillBlanks(int assignment[],int processorCount) {
        for (int m= 0; m<assignment.length;m++ )
        {
            if (assignment[m]==-1) {
                assignment[m] = RandUtil.randInt(processorCount);
            }
        }
    }

    private int[][] calculateMinAssignments() {
        int minAssignments[][] = new int[ Math.min(n,moduleMinimums.length) ][2];

        int[][] cmoduleMinimums= new int[moduleMinimums.length][moduleMinimums[0].length] ;
        ArrayUtil.arrayCopy(moduleMinimums,cmoduleMinimums);

        Arrays.sort(cmoduleMinimums, new Comparator<int[]>() {
            @Override
            public int compare(final int[] entry1, final int[] entry2) {
                final Integer time1 = entry1[2];
                final Integer time2 = entry2[2];
                return time1.compareTo(time2);
            }
        });

        for(int ma = 0; ma<minAssignments.length;ma++)
        {
            minAssignments[ma][1] = cmoduleMinimums[ma][2];
            minAssignments[ma][0] = cmoduleMinimums[ma][0];
        }
        return minAssignments;
    }

    @Override
    public void update(OptimizationProblem problem, Individual i, Individual best) {
        MOTAProblem motaProblem = (MOTAProblem)problem;
        IntegerAssignment assignment = (IntegerAssignment)i.getRepresentation();

        for(int module =0; module<motaProblem.getModuleCount(); module++)
        {
            if((frequencyMatrix[module][assignment.get(module)]++)== 0)
                nonZeroEntryCount++;
            if (moduleMinimums[module][1]== assignment.get(module))
            {
                updateModuleMinimum(module);
            }
        }
    }

    private void updateModuleMinimum(int module) {
        moduleMinimums[module][2] = frequencyMatrix[module][0];
        moduleMinimums[module][1] = 0;
        for (int p=1; p<frequencyMatrix[0].length;p++)
        {
            if (frequencyMatrix[module][p]<moduleMinimums[module][2])
            {
                moduleMinimums[module][1]= p;
                moduleMinimums[module][2]= frequencyMatrix[module][p];
            }
        }
    }

    @Override
    public void init(OptimizationProblem problem, boolean clearMemory) {
        MOTAProblem motaProblem = (MOTAProblem) problem;
        if (clearMemory || frequencyMatrix ==null) {
            frequencyMatrix = new int[motaProblem.getModuleCount()][motaProblem.getProcessorCount()];
            moduleMinimums = new int[motaProblem.getModuleCount()][3];
            for (int m = 0; m < motaProblem.getModuleCount(); m++)
                moduleMinimums[m][0] = m;
        }
        nonZeroEntryCount =0;
    }

    @Override
    public boolean ready() {
        double usage= ((double)nonZeroEntryCount/(double) (frequencyMatrix.length*frequencyMatrix[0].length));
        return usage>READY_THRESHOLD;
    }
}
