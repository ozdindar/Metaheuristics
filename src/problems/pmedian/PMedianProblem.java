package problems.pmedian;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.crossover.OneCutCrossover;
import metaheuristic.ea.parentselector.RouletteWheelParentSelector;
import metaheuristic.ea.terminalcondition.IterationBasedTC;
import metaheuristic.sa.LineerCooling;
import metaheuristic.sa.SA;
import problems.base.InitialSolutionGenerator;
import metrics.stn.*;
import metrics.partitioning.AgglomerativeClusterSSP;
import metrics.partitioning.BinaryStringPartitioner;
import representation.BinaryString;
import representation.base.Representation;
import metrics.partitioning.NullSSP;
import metrics.partitioning.SearchSpacePartitioner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PMedianProblem implements OptimizationProblem {

    private final double[][] distanceMatrix;
    private final int p;

    public PMedianProblem(double[][] distanceMatrix, int p) {
        this.distanceMatrix = distanceMatrix;
        this.p = p;
    }

    @Override
    public boolean isFeasible(Representation r) {
        if (!(r instanceof BinaryString)) return false;
        String code = ((BinaryString) r).toString();
        if (code.length() != distanceMatrix.length) return false;

        long count = code.chars().filter(c -> c == '1').count();
        return count == p;
    }

    @Override
    public double cost(Representation r) {
        if (!isFeasible(r)) return Double.MAX_VALUE;

        String code = ((BinaryString) r).toString();
        double totalCost = 0.0;

        for (int i = 0; i < distanceMatrix.length; i++) {
            double minDist = Double.MAX_VALUE;
            for (int j = 0; j < distanceMatrix.length; j++) {
                if (code.charAt(j) == '1') {
                    minDist = Math.min(minDist, distanceMatrix[i][j]);
                }
            }
            totalCost += minDist;
        }

        return totalCost;
    }

    @Override
    public double maxDistance() {
        double max = 0.0;
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                max = Math.max(max, distanceMatrix[i][j]);
            }
        }
        return max;
    }

    @Override
    public int getDimension() {
        return distanceMatrix.length;
    }

    public int getP() {
        return p;
    }


    private static AbstractMetaheuristic createGA() {
        MutationOperator mo = new RandomFlip(3);
        CrossOverOperator co = new OneCutCrossover();
        TerminalCondition tc = new IterationBasedTC(10000);

        EA ea = new EA(Arrays.asList(co),
                Arrays.asList(mo),
                new RouletteWheelParentSelector(2),
                tc);

        return ea;
    }
    private static AbstractMetaheuristic createSA()
    {
        MutationOperator mo = new PMedianRandomSwap(1);
        SA sa = new SA(Arrays.asList(mo),new LineerCooling(0.001),10,10000,0.0);
        return sa;
    }

    private static void demo() throws IOException {


        AbstractMetaheuristic alg = createSA();

        OptimizationProblem problem =  PMedianInstanceLoader.loadFromFile("./input/pmed6.txt");

        int slotCount =4;
        BinaryStringPartitioner ssp = new BinaryStringPartitioner(8,slotCount);

        InitialSolutionGenerator isg = new PMedianRandomISG();

        STNBuilder stnBuilder = new STNBuilder();
        stnBuilder.init(problem,ssp);
        alg.addListener(stnBuilder);
        alg.perform((OptimizationProblem) problem,isg);

        STN stn = stnBuilder.getSTN();
        Predicate<STNNode> filter = (x)->x.getbCost()<8000;

        System.out.println("Constructing visualizer..");

        //STNUtils.printStats(metrics.stn, filter);

        STNVisualizer.visualize(stn, filter);



    }

    private static void demoAggloClustering() throws IOException {


        AbstractMetaheuristic alg = createSA();

        OptimizationProblem problem =  PMedianInstanceLoader.loadFromFile("./input/pmed1.txt");


        SearchSpacePartitioner nullSSP = new NullSSP();

        InitialSolutionGenerator isg = new PMedianRandomISG();

        STNBuilder stnBuilder = new STNBuilder();
        stnBuilder.init(problem,nullSSP);
        alg.addListener(stnBuilder);
        System.out.println("SA in progress..");
        alg.perform((OptimizationProblem) problem,isg);

        STN stn = stnBuilder.getSTN();
        List<String> elements = stn.getNodes().stream().map(STNNode::getId).collect(Collectors.toList());
        System.out.println("Partitioner is being created..");
        SearchSpacePartitioner ssp= AgglomerativeClusterSSP.from(elements,10);

        System.out.println("STN is being merged..");
        STN updatedSTN = STNBuilder.buildFromExisting(stn,ssp);



        System.out.println("Constructing visualizer..");

        //STNUtils.printStats(metrics.stn, filter);

        STNVisualizer.visualize(updatedSTN);



    }

    public static void main(String[] args) throws IOException {
        demoAggloClustering();
    }

}
