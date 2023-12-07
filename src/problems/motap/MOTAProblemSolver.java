package problems.motap;

import base.NeighboringFunction;
import base.TerminalCondition;
import experiments.motap.MOTAPGenerator;
import gui.MHRunnerPanel;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.ConsoleMetaheuristicListener;
import metaheuristic.HybridEA.HybridEA;
import metaheuristic.RepeatingMetaHeuristicEngine;
import metaheuristic.bee.BeesAlgorithm;
import metaheuristic.dpso.DPSO;
import metaheuristic.ea.EA;
import metaheuristic.ea.IslandEA;
import metaheuristic.ea.SimpleMutationSearch;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.crossover.SimplePermutationCrossover;
import metaheuristic.ea.mutation.InsertMutation;
import metaheuristic.ea.mutation.RandomInterchange;
import metaheuristic.ea.parentselector.CostBasedParentSelector;
import metaheuristic.ea.terminalcondition.*;
import metaheuristic.es.ES;
import metaheuristic.hbmo.HBMO;
import metaheuristic.island.GenericIsland;
import metaheuristic.island.IslandModul;
import metaheuristic.mbo.IslandMBO;
import metaheuristic.mbo.MBO;
import metaheuristic.mbo.MBO2;
import metaheuristic.mbo.PMBO;
import metaheuristic.sa.GeometricCooling;
import metaheuristic.sa.SA;
import metaheuristic.sos.SOS;
import metaheuristic.ss.ScatterSearch;
import metaheuristic.ss2.ScatterSearch2;
import metaheuristic.tabu.SolutionMemoryTabuList;
import metaheuristic.tabu.TabuSearch;
import metaheuristic.tabu.mediumtermmemory.MOTAPFrequencyMatrix;
import metaheuristic.tabu.mediumtermmemory.MOTAPRecencyMatrix;
import problems.base.InitialSolutionGenerator;
import problems.motap.crossover.SimpleMOTACrossOver;
import problems.motap.mutation.*;
import problems.motap.solutionGenerator.MOTAPRandomSG;
import problems.motap.solutionGenerator.RandomLoadBalancedSG;
import problems.nqueen.NQProblem;
import problems.nqueen.RandomNQueenSG;
import representation.IntegerAssignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class MOTAProblemSolver {


    public MOTAProblemSolver() {

    }

    public void solveWithEA()
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimpleMOTACrossOver());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new SimpleMOTAPMutation(5));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new IterationBasedTC(100000);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        EA alg = new EA(coList, muList, ps,tc);
        alg.addListener(new ConsoleMetaheuristicListener());

        DCS dcs= MOTAPGenerator.createRandomDCS(MOTAPGenerator.PROCESSOR_COUNT, 50, 100);
        Task task = MOTAPGenerator.createRandomTask(MOTAPGenerator.MODULE_COUNT, MOTAPGenerator.PROCESSOR_COUNT, 0.8,1);

        //DCS dcs= createSampleDCS();
        //Task task = createSampleTask();

        MOTAProblem p = new MOTAProblem(dcs,task);


        alg.perform(p,new MOTAPRandomSG());
    }

    public void solveWithSOS()
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimplePermutationCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RandomInterchange());
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new IterationBasedTC(100000);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        SOS alg = new SOS(coList, muList, ps,tc);
        alg.addListener(new ConsoleMetaheuristicListener());

        NQProblem p = new NQProblem(10);

        alg.perform(p,new RandomNQueenSG());
    }

    private void solveWithBA() {

        int  maxNeighboringCount= 80000;
        DCS dcs= MOTAPGenerator.createRandomDCS(16);
        Task task = MOTAPGenerator.createRandomTask(16, 16, 0.8);
        MOTAProblem p = new MOTAProblem(dcs,task);

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new ProcessorRelaxingMutation(3,0.3));
        muList.add(new ExecutionGreedySwap(3,0.3));
        muList.add(new CommunicationGreedySwap(3,0.3));

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        BeesAlgorithm ba = new BeesAlgorithm(muList,tc,45,3,1,7,2,3);

        ba.perform(p,new MOTAPRandomSG());

        IntegerAssignment allocation = (IntegerAssignment) ba.getBestKnownSolution();

        System.out.println("ExeCost: "+p.dcs.executionCostOf(task, allocation));
        System.out.println("ComCost: "+p.dcs.communicationCostOf(task, allocation));
        System.out.println("ERelCost: "+p.dcs.processReliabilityCostOf(task, allocation));
        System.out.println("CRelCost: "+p.dcs.communicationReliabilityCostOf(task, allocation));
        System.out.println("InfCost: "+p.dcs.infeasibilityFactorOf(task, allocation));
    }


    public void solveWithSA()
    {
        int  maxNeighboringCount= 80000;
        DCS dcs= MOTAPGenerator.createRandomDCS(8);
        Task task = MOTAPGenerator.createRandomTask(20, 8, 0.8);
        MOTAProblem p = new MOTAProblem(dcs,task);

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new ProcessorRelaxingMutation(3,0.3));
        muList.add(new ExecutionGreedySwap(3,0.3));
        muList.add(new CommunicationGreedySwap(3,0.3));

        SA alg = new SA(muList, new GeometricCooling(0.9),0.00001,maxNeighboringCount,0);

        CPLEXSolver.solveMOTAP(p, 3600);

        alg.perform(p,new MOTAPRandomSG());

        IntegerAssignment allocation = (IntegerAssignment) alg.getBestKnownSolution();

        System.out.println("ExeCost: " + p.dcs.executionCostOf(task, allocation));
        System.out.println("ComCost: "+p.dcs.communicationCostOf(task,allocation));
        System.out.println("ERelCost: "+p.dcs.processReliabilityCostOf(task, allocation));
        System.out.println("CRelCost: "+p.dcs.communicationReliabilityCostOf(task, allocation));
        System.out.println("InfCost: "+p.dcs.infeasibilityFactorOf(task, allocation));

    }



    public void solveWithDPSO()
    {
        NQProblem p = new NQProblem(10);

        DPSO dpso = new DPSO(0.6031,0.6485,0.6475,95, new NeighboringBasedTC(10000) );



        dpso.perform(p,new RandomNQueenSG());
    }




    private AbstractMetaheuristic createMetaHeuristicEA(MOTAProblem p, int maxNeighboringCount) {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimpleMOTACrossOver());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        //muList.add(new InsertMutation(1));
        //muList.add(new SimpleMOTAPMutation(1));
        muList.add(new SimpleMOTAPMutation(1));
        //muList.add(new ProcessorRelaxingMutation(1,0.3));
        //muList.add(new ExecutionGreedySwap(1,0.3));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        EA alg = new EA(coList, muList, ps,tc);


        alg.setInitialPopulationSize(100);

        return alg;
    }

    private AbstractMetaheuristic createMetaHeuristic2(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new ProcessorRelaxingMutation(3,0.3));
        muList.add(new ExecutionGreedySwap(3,0.3));
        muList.add(new CommunicationGreedySwap(3,0.3));

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        BeesAlgorithm ba = new BeesAlgorithm(muList,tc,45,40,10,7,2,2);

        return ba;
    }

    private AbstractMetaheuristic createMetaHeuristic3(MOTAProblem p, int maxNeighboringCount) {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        //muList.add(new InsertMutation(1));
        muList.add(new ProcessorRelaxingMutation(3,0.3));
        muList.add(new ExecutionGreedySwap(3,0.3));
        muList.add(new CommunicationGreedySwap(3,0.3));

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        SA alg = new SA(muList, new GeometricCooling(0.9),0.0,maxNeighboringCount,0);
        return alg;
    }




    private AbstractMetaheuristic createMetaHeuristic4(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new InsertMutation(1));

        TerminalCondition tc1 = new IterationBasedTC(100000);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        SimpleMutationSearch alg = new SimpleMutationSearch( muList, tc);
        alg.setInitialPopulationSize(95);

        return alg;
    }

    private AbstractMetaheuristic createMBO(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GreedySwapWithEscape(3));


        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        MBO mbo = new MBO(muList,tc,25,5,5,1);

        return mbo;

    }
    
    
    
    private AbstractMetaheuristic createPMBO(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GreedySwapWithEscape(3));
        //muList.add(new GreedySwap(1));


        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        PMBO pmbo = new PMBO(muList,tc,25,5,5,1);

        return pmbo;

    }
    
    private AbstractMetaheuristic createIslandMBO(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GreedySwapWithEscape(3));
        //muList.add(new GreedySwap(1));


        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        IslandMBO imbo = new IslandMBO(muList,tc,25,5,5,1);

        return imbo;

    }

    private AbstractMetaheuristic createEA(MOTAProblem p, int maxNeighboringCount)
    {
    	List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimpleMOTACrossOver());
        
    	List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GreedySwapWithEscape(3));
        //muList.add(new SimpleMOTAPMutation(1));
        //muList.add(new GreedySwap(1));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));
        
        /*TerminalCondition tc1 = new IterationBasedTC(100000);
        //TerminalCondition tc1 = new IterationBasedTC(100);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));*/

        EA alg = new EA(coList, muList, ps,tc);
        
        alg.setInitialPopulationSize(100);
        return alg;
    }
    
    private AbstractMetaheuristic createIslandEA(MOTAProblem p, int maxNeighboringCount)
    {
    	List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimpleMOTACrossOver());
        
    	List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GreedySwapWithEscape(3));
        //muList.add(new SimpleMOTAPMutation(1));
        //muList.add(new GreedySwap(1));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));
        
        /*TerminalCondition tc1 = new IterationBasedTC(100000);
        //TerminalCondition tc1 = new IterationBasedTC(100);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));*/

        IslandEA alg = new IslandEA(coList, muList, ps,tc);
        
        alg.setInitialPopulationSize(100);
        return alg;
    }
    
    private AbstractMetaheuristic createIslandEA2(MOTAProblem p, int maxNeighboringCount)
    {
    	List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimpleMOTACrossOver());
        
    	List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GreedySwapWithEscape(3));
        //muList.add(new SimpleMOTAPMutation(1));
        //muList.add(new GreedySwap(1));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));
        
        /*TerminalCondition tc1 = new IterationBasedTC(100000);
        //TerminalCondition tc1 = new IterationBasedTC(100);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));*/

        IslandEA alg = new IslandEA(coList, muList, ps,tc);
        
        alg.setInitialPopulationSize(25);
        return alg;
    }
    
    private AbstractMetaheuristic createES(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GRMR_ES(3));

        TerminalCondition tcIteration = new IterationBasedTC(10000);
        TerminalCondition tcCost  = new CostBasedTC(0);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcCost,tcIteration));


        ES es = new ES(20,140,muList,tc);
        es.setDiversityMemory(new MOTAPFrequencyMatrix(25));


        return es;

    }

    private AbstractMetaheuristic createScatterSearch(MOTAProblem p, int maxNeighboringCount) {

        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new GRMR_Tabu(3,5));

        TerminalCondition tcIteration = new IterationBasedTC(20);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,5);
        ts.setIntensityMemory(new MOTAPRecencyMatrix(20));
        ts.setDiversityMemory(new MOTAPFrequencyMatrix(30));
        ts.setName("TABU2");
        ts.setClearMemoryAtInit(false);

        TerminalCondition tcIteration2 = new IterationBasedTC(200);
        //TerminalCondition tcIteration2 = new CPUTimeBasedTC(5000);
        ScatterSearch ss = new ScatterSearch(ts,new SimpleMOTACrossOver(),tcIteration2);


        //ts.addListener(new ConsoleMetaheuristicListener());


        return ss;

    }

    private AbstractMetaheuristic createScatterSearch2(MOTAProblem p, int maxNeighboringCount) {

        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new GRMR_Tabu(3,5));

        TerminalCondition tcIteration = new IterationBasedTC(20);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,5);
        ts.setIntensityMemory(new MOTAPRecencyMatrix(20));
        ts.setDiversityMemory(new MOTAPFrequencyMatrix(30));
        ts.setName("TABU2");
        ts.setClearMemoryAtInit(false);

        TerminalCondition tcIteration2 = new IterationBasedTC(200);
        //TerminalCondition tcIteration2 = new CPUTimeBasedTC(5000);
        ScatterSearch2 ss = new ScatterSearch2(ts,new SimpleMOTACrossOver(),tcIteration2);


        //ts.addListener(new ConsoleMetaheuristicListener());


        return ss;

    }

    private AbstractMetaheuristic createMetaHeuristicHEA(MOTAProblem p, int maxNeighboringCount) {

        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new GRMR_Tabu(3,5));

        TerminalCondition tcIteration = new IterationBasedTC(20);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,5);
        ts.setIntensityMemory(new MOTAPRecencyMatrix(20));
        ts.setDiversityMemory(new MOTAPFrequencyMatrix(30));
        ts.setName("TABU2");
        ts.setClearMemoryAtInit(false);

        TerminalCondition tcIteration2 = new IterationBasedTC(200);
        //TerminalCondition tcIteration2 = new CPUTimeBasedTC(5000);

        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimpleMOTACrossOver());

        HybridEA hea = new HybridEA(ts,coList,null,new CostBasedParentSelector(),tcIteration2);


        //ts.addListener(new ConsoleMetaheuristicListener());


        return hea;

    }

    private AbstractMetaheuristic createTabuSearchNaive(MOTAProblem p, int maxNeighboringCount) {

        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new GRMR_Tabu());

        TerminalCondition tcIteration = new IterationBasedTC(10000);
        TerminalCondition tcCost  = new CostBasedTC(0);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcCost,tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,5);
        ts.setName("TABU-SIMPLE");
        //ts.setIntensityMemory(new MOTAPRecencyMatrix(10));
        //ts.setDiversityMemory(new MOTAPFrequencyMatrix(40));
        //ts.addListener(new ConsoleMetaheuristicListener());


        return ts;

    }

    private AbstractMetaheuristic createTabuSearchSimple(MOTAProblem p, int maxNeighboringCount) {

        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new GRMR_Tabu());

        TerminalCondition tcIteration = new IterationBasedTC(10000);
        TerminalCondition tcCost  = new CostBasedTC(0);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcCost,tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,5);
        ts.setIntensityMemory(new MOTAPRecencyMatrix(20));
        ts.setDiversityMemory(new MOTAPFrequencyMatrix(30));
        ts.setName("TABU2");
        ts.setClearMemoryAtInit(false);


        return ts;

    }

    private AbstractMetaheuristic createTabuSearch(MOTAProblem p, int maxNeighboringCount) {

        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new GRMR_Tabu(3,5));

        TerminalCondition tcIteration = new IterationBasedTC(10000);
        TerminalCondition tcCost  = new CostBasedTC(0);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcCost,tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,5);
        ts.setIntensityMemory(new MOTAPRecencyMatrix(20));
        ts.setDiversityMemory(new MOTAPFrequencyMatrix(30));
        ts.setName("TABU-SMART");
        //ts.addListener(new ConsoleMetaheuristicListener());


        return ts;

    }

    private AbstractMetaheuristic createSmartMBO(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        //muList.add(new ProcessorRelaxingMutation(3,0.3));
        //muList.add(new ExecutionGreedySwap(3,0.3));
        //muList.add(new CommunicationGreedySwap(3,0.3));
        muList.add(new GreedySwapWithEscape(1));
        muList.add(new SimpleMOTAPMutation(1));


        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        MBO mbo = new MBO(muList,tc,25,5,5,1);
        mbo.setName("Smart-MBO");

        return mbo;

    }

    private AbstractMetaheuristic createMBO2(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GreedySwapWithEscape(3));
        //muList.add(new SimpleMOTAPMutation(1));
        //muList.add(new SimpleMOTAPMutation(1));


        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        MBO2 mbo = new MBO2(muList,tc,25,5,5,1);
        mbo.setName("MBO2");

        return mbo;

    }

    private AbstractMetaheuristic createHBMO(MOTAProblem p, int maxNeighboringCount) {

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        //muList.add(new ProcessorRelaxingMutation(3,0.3));
        //muList.add(new ExecutionGreedySwap(3,0.3));
        muList.add(new GreedySearch());
        //muList.add(new GreedySwap(3));
        //muList.add(new SimpleMOTAPMutation(1));

        TerminalCondition tc3= new CPUTimeBasedTC(1000);
        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        HBMO hbmo = new HBMO(muList,tc3,new SimpleMOTACrossOver(),35,10,3,0.1);


        return hbmo;

    }

    private AbstractMetaheuristic createIslandHBMO(MOTAProblem p, int maxNeighboringCount) {
        int moduleCount = 8;
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        //muList.add(new ProcessorRelaxingMutation(3,0.3));
        //muList.add(new ExecutionGreedySwap(3,0.3));
        muList.add(new GreedySearch());
        //muList.add(new GreedySwap(3));
        //muList.add(new SimpleMOTAPMutation(1));

        TerminalCondition tc3 = new CPUTimeBasedTC(5000);
        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        HBMO hbmo = new HBMO(muList,tc,new SimpleMOTACrossOver(),35,10,3,0.1);

        List<IslandModul> modules = new ArrayList<>();
        for (int i = 0; i < moduleCount; i++) {
            modules.add( new HBMO(muList,tc3,new SimpleMOTACrossOver(),35,10,3,0.1));
        }


        //modules.add( new PHBMO(muList,tc,new SimpleMOTACrossOver(),30,10,3,0.1));
        //modules.add( new PHBMO(muList,tc,new SimpleMOTACrossOver(),30,10,3,0.1));
        GenericIsland gi = new GenericIsland(tc3,modules,moduleCount,10,5,4);


        return gi;

    }

    void solveWithGUI()
    {
        int  maxNeighboringCount= 15000;
        DCS dcs= MOTAPGenerator.createRandomDCS(8);
        Task task = MOTAPGenerator.createRandomTask(80,8, 0.8);

        //DCS dcs= createSampleDCS();
        //Task task = createSampleTask();

        MOTAProblem p = new MOTAProblem(dcs,task);

        AbstractMetaheuristic ea = createMetaHeuristicEA(p, maxNeighboringCount);
        AbstractMetaheuristic hea = createMetaHeuristicHEA(p, maxNeighboringCount);
        AbstractMetaheuristic alg2 = createMetaHeuristic2(p, maxNeighboringCount);
        AbstractMetaheuristic alg3 = createMetaHeuristic3(p, maxNeighboringCount);
        AbstractMetaheuristic alg4 = createMetaHeuristic4(p, maxNeighboringCount);
        AbstractMetaheuristic mbo = createMBO(p, maxNeighboringCount);
        AbstractMetaheuristic pmbo = createPMBO(p, maxNeighboringCount);
        AbstractMetaheuristic island = createIslandEA(p, maxNeighboringCount);
        
        AbstractMetaheuristic smbo =  createSmartMBO(p, maxNeighboringCount);
        AbstractMetaheuristic mbo2 = createMBO2(p, maxNeighboringCount);
        AbstractMetaheuristic hbmo = createHBMO(p, maxNeighboringCount);
        AbstractMetaheuristic tabu3 = createTabuSearchNaive(p, maxNeighboringCount);
        AbstractMetaheuristic tabu2 = createTabuSearchSimple(p, maxNeighboringCount);
        AbstractMetaheuristic tabu1 = createTabuSearch(p, maxNeighboringCount);

        AbstractMetaheuristic es = createES(p, maxNeighboringCount);

        AbstractMetaheuristic ss1 = createScatterSearch(p, maxNeighboringCount);
        AbstractMetaheuristic ss2 = createScatterSearch2(p, maxNeighboringCount);

        AbstractMetaheuristic islanhbmo = createIslandHBMO(p, maxNeighboringCount);

        List<AbstractMetaheuristic> algs = Arrays.asList(islanhbmo);

        RandomLoadBalancedSG isg_es = new RandomLoadBalancedSG();
        isg_es.setUseMutationSize(true);
        List<InitialSolutionGenerator>solutionGenerators = Arrays.asList(new RandomLoadBalancedSG(), new MOTAPRandomSG());

        MHRunnerPanel.showComparison("Metaheuristic Performance Comparison", algs,solutionGenerators, p);


        //CPLEXSolver.solveMOTAP(p);
    }



    void solveWithRepeats()
    {
        int  maxNeighboringCount= 15000;
        DCS dcs= MOTAPGenerator.createRandomDCS(8);
        Task task = MOTAPGenerator.createRandomTask(80,8, 0.8);

        //DCS dcs= createSampleDCS();
        //Task task = createSampleTask();

        MOTAProblem p = new MOTAProblem(dcs,task);


        AbstractMetaheuristic mbo = createMBO(p, maxNeighboringCount);
        AbstractMetaheuristic pmbo = createPMBO(p, maxNeighboringCount);
        AbstractMetaheuristic island = createIslandEA(p, maxNeighboringCount);
        AbstractMetaheuristic island2 = createIslandEA2(p, maxNeighboringCount);
        AbstractMetaheuristic ea = createEA(p, maxNeighboringCount);
        
        AbstractMetaheuristic islandmbo = createIslandMBO(p, maxNeighboringCount);

        AbstractMetaheuristic hbmo = createHBMO(p, maxNeighboringCount);
        AbstractMetaheuristic islandhbmo = createIslandHBMO(p, maxNeighboringCount);

        List<AbstractMetaheuristic> algs = Arrays.asList(pmbo,mbo);


        List<InitialSolutionGenerator>solutionGenerators = Arrays.asList(new RandomLoadBalancedSG(), new MOTAPRandomSG());

        RepeatingMetaHeuristicEngine rmeEA = new RepeatingMetaHeuristicEngine(ea,p,new RandomLoadBalancedSG(),"ea_out.txt","TEST","",10);
        RepeatingMetaHeuristicEngine rmeIsland = new RepeatingMetaHeuristicEngine(island,p,new RandomLoadBalancedSG(),"island_out.txt","TEST","",10);
        RepeatingMetaHeuristicEngine rmeIsland2 = new RepeatingMetaHeuristicEngine(island2,p,new RandomLoadBalancedSG(),"island2_out.txt","TEST","",10);
        RepeatingMetaHeuristicEngine rmePMBO = new RepeatingMetaHeuristicEngine(pmbo,p,new RandomLoadBalancedSG(),"pmbo_out.txt","TEST","",10);
        RepeatingMetaHeuristicEngine rmeMBO = new RepeatingMetaHeuristicEngine(mbo,p,new RandomLoadBalancedSG(),"mbo_out.txt","TEST","",10);
        RepeatingMetaHeuristicEngine rmeIMBO = new RepeatingMetaHeuristicEngine(islandmbo,p,new RandomLoadBalancedSG(),"islandmbo_out.txt","TEST","",10);

        RepeatingMetaHeuristicEngine rmeIslandHBMO = new RepeatingMetaHeuristicEngine(islandhbmo,p,new MOTAPRandomSG(),"islandmbo_out.txt","TEST","",10);
        RepeatingMetaHeuristicEngine rmeHBMO = new RepeatingMetaHeuristicEngine(hbmo,p,new MOTAPRandomSG(),"islandmbo_out.txt","TEST","",10);

        rmeHBMO.run();

        //rmeEA.run();
        //rmeIsland.run();
        //rmeIsland2.run();
        
        //rmeMBO.run();
        //rmePMBO.run();
        //rmeIMBO.run();
        //MHRunnerPanel.showComparison("Metaheuristic Performance Comparison", algs,solutionGenerators, p);


        //CPLEXSolver.solveMOTAP(p);
    }


    public static void main(String args[])
    {
        MOTAProblemSolver ns = new MOTAProblemSolver();
        //ns.solveWithTabuSearch();
        //ns.solveWithSOS();
        ns.solveWithRepeats();

        //ns.solveWithIslandEA();
        //ns.solveWithEA();
       // ns.solveWithRepeats();
        //ns.solveWithSA();
        //ns.solveWithBA();

        try {
        //    ns.optimize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
