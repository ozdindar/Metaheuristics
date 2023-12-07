package problems.mccdp.test;


import base.TerminalCondition;
import gui.MHListener;
import gui.MHRunnerPanel;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.HybridEA.HybridEA;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.crossover.SinglePointCrossover;
import metaheuristic.ea.mutation.RandomRemove;
import metaheuristic.ea.parentselector.TournamentParentSelector;
import metaheuristic.ea.terminalcondition.CostBasedTC;
import metaheuristic.ea.terminalcondition.NeighboringBasedTC;
import metaheuristic.ea.terminalcondition.OrCompoundTC;
import metaheuristic.mbo.MBO;
import metaheuristic.sa.LineerCooling;
import metaheuristic.sa.SA;
import problems.base.InitialSolutionGenerator;
import problems.mccdp.*;
import problems.mccdp.mutation.MCCDPRandomChange;
import problems.mccdp.mutation.MCCDPSmartChange;
import problems.mccdp.mutation.RandomInsert;
import problems.mccdp.solutiongenerator.SmartSG;
import representation.IntegerVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 31.05.2017.
 */
public class MCCDPTest {


    private AbstractMetaheuristic createMetaHeuristicEA(MCCDP p, int maxNeighboringCount) {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SinglePointCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new MCCDPRandomChange());
        muList.add(new RandomRemove());
        muList.add(new RandomInsert());


        ParentSelector ps = new TournamentParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        EA alg = new EA(coList, muList, ps,tc);


        alg.setInitialPopulationSize(95);

        return alg;
    }

    private AbstractMetaheuristic createMetaHeuristicHEA(MCCDP p, int maxNeighboringCount) {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SinglePointCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new MCCDPRandomChange());
        muList.add(new RandomRemove());
        muList.add(new RandomInsert());


        ParentSelector ps = new TournamentParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        List<MutationOperator> neighboringList = new ArrayList<MutationOperator>();
        neighboringList.add(new MCCDPSmartChange());



        SA sa = new SA(neighboringList,new LineerCooling(1),0,maxNeighboringCount,0);

        HybridEA alg = new HybridEA(sa,coList, muList, ps,tc);




        alg.setInitialPopulationSize(95);

        return alg;
    }

    private AbstractMetaheuristic createMetaHeuristicSA(MCCDP p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new MCCDPRandomChange());
        muList.add(new RandomRemove());



        SA alg = new SA(muList,new LineerCooling(0.001),0,maxNeighboringCount,0);

        return alg;
    }

    private AbstractMetaheuristic createMetaHeuristicMBO(long maxNeighboringCount )
    {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new MCCDPRandomChange());
        muList.add(new RandomRemove());
        muList.add(new RandomInsert());

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        MBO alg = new MBO(muList,tc1,51,10,3,1);

        return alg;
    }


    void solveWithGUI(AbstractMetaheuristic alg,MCCDP p)
    {
        List<InitialSolutionGenerator>solutionGenerators = Arrays.asList(new SmartSG());


        WSNModelViewer viewer =WSNModelViewer.showModel(alg.getName(),p.getWsnModel(),(IntegerVector) p.decode(alg.getBestKnownSolution()),2);

        MHListener listener = new MHListener(viewer);
        InitialSolutionGenerator solutionGenerator = solutionGenerators.get(0);
        MHRunnerPanel.showExecution(alg.getName(), alg,solutionGenerator, p, listener);

    }

    public static void main(String[] args) {
        MCCDPTest test= new MCCDPTest();

        int  maxNeighboringCount= 150000;

        int nodeDistance = 10;
        int nodeCount = 20;


        WSNModel model = new WSNGridModel(nodeCount,nodeCount,nodeCount*nodeDistance,nodeCount*nodeDistance,nodeDistance*3);
        WSNModel model2 = WSNModelFactory.generateRandomListModel(nodeCount*nodeDistance,nodeCount*nodeDistance,nodeCount*nodeCount,nodeCount*nodeCount,nodeDistance*3);

        MCCDP p = new MCCDP(model2);



        //test(p);

        AbstractMetaheuristic ea = test.createMetaHeuristicEA(p, maxNeighboringCount);
        AbstractMetaheuristic sa = test.createMetaHeuristicSA(p, maxNeighboringCount);
        AbstractMetaheuristic mbo = test.createMetaHeuristicMBO(maxNeighboringCount);
        AbstractMetaheuristic hea = test.createMetaHeuristicHEA(p,maxNeighboringCount);
        //test.solveWithGUI(ea,p);
        //test.solveWithGUI(sa,p);
        //test.solveWithGUI(mbo,p);
        test.solveWithGUI(hea,p);

    }

    private static void test(MCCDP p) {
        IntegerVector iv1 = new IntegerVector(Arrays.asList(1,2,3,4,5,6,10));
        IntegerVector iv2 = new IntegerVector(Arrays.asList(2,3,4,5,6,10));

        WSNModelViewer.showModel("1",p.getWsnModel(),iv1);
        WSNModelViewer.showModel("2",p.getWsnModel(),iv2);

        System.out.println(p.cost(iv1));
        System.out.println(p.cost(iv2));
        return;
    }

}
