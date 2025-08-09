package problems.raps.test;

import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import gui.MHRunnerPanel;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.HybridEA.HybridEA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.parentselector.CostBasedParentSelector;
import metaheuristic.ea.terminalcondition.NeighboringBasedTC;
import metaheuristic.es.ES;
import metaheuristic.ls.LocalSearch;
import metaheuristic.mbo.MBO2;
import metaheuristic.sa.GeometricCooling;
import metaheuristic.sa.SA;
import metaheuristic.sa.SA_NF;
import problems.base.InitialSolutionGenerator;
import problems.raps.RAPBenchmarks;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import problems.raps.crossover.RAPSimpleCrossover;
import problems.raps.initialsolutiongenerator.RAPSRandomSG;
import problems.raps.mutation.RAPRandomCountChangeMutation;
import problems.raps.mutation.RAPRandomTypeChangeMutation;
import problems.raps.neighboringFunction.RAPCostWeightBalancingNF;
import problems.raps.neighboringFunction.RAPRandomCountChangeNF;
import problems.raps.neighboringFunction.RAPRandomTypeChangeNF;
import problems.raps.neighboringFunction.RAPReliabilityBalancingNF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 7/4/2017.
 */
public class RAPSTest {

    private AbstractMetaheuristic createSA(RAPS p, int maxNeighboringCount) {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomCountChangeMutation());

        SA alg = new SA(muList, new GeometricCooling(0.9),0.0,maxNeighboringCount,0);
        return alg;
    }

    private AbstractMetaheuristic createES(RAPS p, int maxNeighboringCount) {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomCountChangeMutation());


        TerminalCondition tc = new NeighboringBasedTC(maxNeighboringCount);


        ES es = new ES(20,140,muList,tc);
        return es;
    }

    private AbstractMetaheuristic createSA_NF(RAPS p, int maxNeighboringCount) {
        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new RAPRandomCountChangeNF());
        muList.add(new RAPRandomTypeChangeNF());


        SA_NF alg = new SA_NF(muList, new GeometricCooling(0.9),0.0,maxNeighboringCount,0);
        return alg;
    }



    private AbstractMetaheuristic createMBO2(OptimizationProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomCountChangeMutation());
        muList.add(new RAPRandomTypeChangeMutation());


        TerminalCondition tc = new NeighboringBasedTC(maxNeighboringCount);


        MBO2 mbo = new MBO2(muList,tc,25,5,5,1);
        mbo.setName("MBO2");

        return mbo;

    }

    private AbstractMetaheuristic createHEA(OptimizationProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomTypeChangeMutation());
        muList.add(new RAPRandomCountChangeMutation());

        TerminalCondition tcIteration = new NeighboringBasedTC(maxNeighboringCount);

        List<NeighboringFunction> nfList = new ArrayList<NeighboringFunction>();
        nfList.add(new RAPReliabilityBalancingNF());
        nfList.add(new RAPCostWeightBalancingNF());

        AbstractSMetaheuristic ls = new LocalSearch(nfList);


        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new RAPSimpleCrossover());

        HybridEA hea = new HybridEA(ls,coList,muList,new CostBasedParentSelector(),tcIteration);


        return hea;

    }

    void  testSolution()
    {
        int system[][] = {  {0,0,3,0},{2,0,0},{0,0,0,3},{0,0,3},{0,3,0},
                            {0,2,0,0},{2,0,0},{4,0,0},{0,0,2,0},{0,3,0},
                            {2,0,0},{4,0,0,0},{0,2,0},{0,0,2,0}};
        RAPS rap = RAPBenchmarks.createBMInstance(100,130,170);
        RAPSSolution solution = new RAPSSolution(rap,system);
        System.out.println(solution);
    }



    void solveWithGUI()
    {
        int  maxNeighboringCount= 550000;

        RAPS p = RAPBenchmarks.createBMInstance(100,130,170);


        AbstractMetaheuristic sa = createSA(p, maxNeighboringCount);
        AbstractMetaheuristic sa_nf = createSA_NF(p, maxNeighboringCount);


        AbstractMetaheuristic es = createES(p, maxNeighboringCount);
        AbstractMetaheuristic mbo = createMBO2(p, maxNeighboringCount);
        AbstractMetaheuristic hea = createHEA(p, maxNeighboringCount);

        List<AbstractMetaheuristic> algs = Arrays.asList(hea);


        List<InitialSolutionGenerator>solutionGenerators = Arrays.asList(new RAPSRandomSG(),new RAPSRandomSG());

        MHRunnerPanel.showComparison("Metaheuristic Performance Comparison", algs,solutionGenerators, p);



    }

    public static void main(String[] args) {
        RAPSTest rapTest = new RAPSTest();

        rapTest.solveWithGUI();
    }

}
