package experiments.rap;

import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.HybridEA.HybridEA;
import metaheuristic.MetaHeuristic;
import metaheuristic.RepeatingMetaHeuristicEngine;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.parentselector.TournamentParentSelector;
import metaheuristic.ea.terminalcondition.CPUTimeBasedTC;
import metaheuristic.ls.LocalSearch;
import metaheuristic.mbo.MBO_NF;
import metaheuristic.ss2.ScatterSearch2;
import problems.base.InitialSolutionGenerator;
import problems.raps.RAPBenchmarks;
import problems.raps.crossover.RAPSimpleCrossover;
import problems.raps.initialsolutiongenerator.RAPSRandomSG;
import problems.raps.mutation.RAPRandomCountChangeMutation;
import problems.raps.mutation.RAPRandomTypeChangeMutation;
import problems.raps.neighboringFunction.RAPCostWeightBalancingNF;
import problems.raps.neighboringFunction.RAPRandomCountChangeNF;
import problems.raps.neighboringFunction.RAPRandomTypeChangeNF;
import problems.raps.neighboringFunction.RAPReliabilityBalancingNF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dindar.oz on 7/11/2017.
 */
public class RAPTestCaseRunner {

    public static ArrayList<Runnable> testCases = new ArrayList();
    private static int repeatCount=1;

    ExecutorService threadPool = Executors.newFixedThreadPool(1);

    private static AbstractMetaheuristic createHEA(OptimizationProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomTypeChangeMutation());
        muList.add(new RAPRandomCountChangeMutation());

        TerminalCondition tcIteration = new CPUTimeBasedTC(130000);

        List<NeighboringFunction> nfList = new ArrayList<NeighboringFunction>();
        nfList.add(new RAPReliabilityBalancingNF());
        nfList.add(new RAPCostWeightBalancingNF());

        AbstractSMetaheuristic ls = new LocalSearch(nfList);


        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new RAPSimpleCrossover());

        HybridEA hea = new HybridEA(ls,coList,muList,new TournamentParentSelector(),tcIteration);


        return hea;

    }

    private static AbstractMetaheuristic createSS(OptimizationProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomTypeChangeMutation());
        muList.add(new RAPRandomCountChangeMutation());

        TerminalCondition tcIteration = new CPUTimeBasedTC(130000);

        List<NeighboringFunction> nfList = new ArrayList<NeighboringFunction>();
        nfList.add(new RAPReliabilityBalancingNF());
        nfList.add(new RAPCostWeightBalancingNF());

        AbstractSMetaheuristic ls = new LocalSearch(nfList);


        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new RAPSimpleCrossover());

        ScatterSearch2 ss = new ScatterSearch2(ls,new RAPSimpleCrossover(),tcIteration);


        return ss;

    }

    private static AbstractMetaheuristic createMBO2(OptimizationProblem p, int maxNeighboringCount) {

        List<NeighboringFunction> nfList = new ArrayList<NeighboringFunction>();
        nfList.add(new RAPReliabilityBalancingNF());
        nfList.add(new RAPCostWeightBalancingNF());
        nfList.add(new RAPRandomTypeChangeNF());
        nfList.add(new RAPRandomCountChangeNF());


        TerminalCondition tc = new CPUTimeBasedTC(130000);


        MBO_NF mbo = new MBO_NF(nfList,tc,25,5,5,1);


        return mbo;

    }

    private static void loadTestCases( String outputFileName) throws IOException {

        int C= 130;
        for (int W = 159; W < 192; W++) {
            OptimizationProblem problem = RAPBenchmarks.createBMInstance(100,C,W);
            Runnable solver = generateSolver(problem,"C:"+ C +" W:"+ W+" ",outputFileName);

            testCases.add(solver);
        }


    }


    private static Runnable generateSolver(OptimizationProblem problem, String prefix,String outputFileName) {


        String suffix = "";
        InitialSolutionGenerator solutionGenerator = new RAPSRandomSG();
        MetaHeuristic ms = createHEA(problem,200000);
        //MetaHeuristic ms = createSS(problem,200000);
        return new RepeatingMetaHeuristicEngine(ms,
                problem, solutionGenerator,
                outputFileName,prefix,suffix,repeatCount);

    }

    public void start() throws IOException {
        loadTestCases("rap_output.txt");
        for(int i=0; i<testCases.size(); i++)
            threadPool.execute(testCases.get(i));


        threadPool.shutdown();
        while(!threadPool.isTerminated()){}
        System.out.println("Finished all threads!");

    }

    public static void main(String[] args) throws IOException {
        RAPTestCaseRunner runner = new RAPTestCaseRunner();
        runner.start();
    }
}
