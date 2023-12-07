package problems.rap.test;

import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.terminalcondition.NeighboringBasedTC;
import metaheuristic.es.ES;
import metaheuristic.mbo.MBO2;
import metaheuristic.sa.GeometricCooling;
import metaheuristic.sa.SA;
import metaheuristic.sa.SA_NF;
import problems.base.InitialSolutionGenerator;
import problems.rap.RAP;
import problems.rap.RAPBenchmarks;
import problems.rap.RAPSolution;
import problems.rap.initialsolutiongenerator.RAPRandomSG;
import problems.rap.mutation.RAPRandomAddMutation;
import problems.rap.mutation.RAPRandomRemoveMutation;
import problems.rap.neighboringFunction.RAPRandomAddNF;
import problems.rap.neighboringFunction.RAPRandomRemoveNF;
import problems.rap.neighboringFunction.RAPSmartAddNF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 7/4/2017.
 */
public class RAPTest {

    private AbstractMetaheuristic createSA(RAP p, int maxNeighboringCount) {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomRemoveMutation());
        muList.add(new RAPRandomAddMutation());

        SA alg = new SA(muList, new GeometricCooling(0.9),0.0,maxNeighboringCount,0);
        return alg;
    }

    private AbstractMetaheuristic createES(RAP p, int maxNeighboringCount) {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomRemoveMutation());
        muList.add(new RAPRandomAddMutation());


        TerminalCondition tc = new NeighboringBasedTC(maxNeighboringCount);


        ES es = new ES(20,140,muList,tc);
        return es;
    }

    private AbstractMetaheuristic createSA_NF(RAP p, int maxNeighboringCount) {
        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new RAPRandomRemoveNF());
        muList.add(new RAPRandomAddNF());

        SA_NF alg = new SA_NF(muList, new GeometricCooling(0.9),0.0,maxNeighboringCount,0);
        return alg;
    }

    private AbstractMetaheuristic createSA_NF_Smart(RAP p, int maxNeighboringCount) {
        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new RAPRandomRemoveNF());
        muList.add(new RAPSmartAddNF());

        SA_NF alg = new SA_NF(muList, new GeometricCooling(0.9),0.0,maxNeighboringCount,0);
        alg.setName("SA_NF_Smart");
        return alg;
    }


    private AbstractMetaheuristic createMBO2(OptimizationProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RAPRandomAddMutation());
        muList.add(new RAPRandomRemoveMutation());


        TerminalCondition tc = new NeighboringBasedTC(maxNeighboringCount);


        MBO2 mbo = new MBO2(muList,tc,25,5,5,1);
        mbo.setName("MBO2");

        return mbo;

    }



    void  testSolution()
    {
        int system[][] = {  {0,0,3,0},{2,0,0},{0,0,0,3},{0,0,3},{0,3,0},
                            {0,2,0,0},{2,0,0},{4,0,0},{0,0,2,0},{0,3,0},
                            {2,0,0},{4,0,0,0},{0,2,0},{0,0,2,0}};
        RAP rap = RAPBenchmarks.createBMInstance(100,130,170);
        RAPSolution solution = new RAPSolution(rap,system);
        System.out.println(solution);
    }



    void solveWithGUI()
    {
        int  maxNeighboringCount= 250000;

        RAP p = RAPBenchmarks.createBMInstance(100,130,191);


        AbstractMetaheuristic sa = createSA(p, maxNeighboringCount);
        AbstractMetaheuristic sa_nf = createSA_NF(p, maxNeighboringCount);
        AbstractMetaheuristic sa_nf_smart = createSA_NF_Smart(p, maxNeighboringCount);

        AbstractMetaheuristic es = createES(p, maxNeighboringCount);
        AbstractMetaheuristic mbo = createMBO2(p, maxNeighboringCount);

        List<AbstractMetaheuristic> algs = Arrays.asList(sa_nf_smart);


        List<InitialSolutionGenerator>solutionGenerators = Arrays.asList(new RAPRandomSG(),new RAPRandomSG());

        //MHRunnerPanel.showComparison("Metaheuristic Performance Comparison", algs,solutionGenerators, p);

        testSolution();

    }

    public static void main(String[] args) {
        RAPTest rapTest = new RAPTest();

        rapTest.solveWithGUI();
    }

}
