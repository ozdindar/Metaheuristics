package problems.pcb;

import base.MutationWrapperNF;
import base.NeighboringFunction;
import base.TerminalCondition;
import metaheuristic.ConsoleShortMetaheuristicListener;
import metaheuristic.dde.DDE;
import metaheuristic.dpso.DPSO;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.crossover.SimplePermutationCrossover;
import metaheuristic.ea.mutation.RandomRemoveReinsert;
import metaheuristic.ea.parentselector.CostBasedParentSelector;
import metaheuristic.ea.terminalcondition.CostBasedTC;
import metaheuristic.ea.terminalcondition.NeighboringBasedTC;
import metaheuristic.ea.terminalcondition.OrCompoundTC;
import metaheuristic.sa.GeometricCooling;
import metaheuristic.sa.SA;
import metaheuristic.sos.ConsolSOSListener;
import metaheuristic.sos.SOS;
import metaheuristic.tabu.SolutionMemoryTabuList;
import metaheuristic.tabu.TabuSearch;
import problems.pcb.mutation.GuidedTwoOpt;
import problems.pcb.terminalcondition.PCBProblemTC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 09.06.2015.
 */
public class PCBSolver implements Runnable{

    private PCBProblem problem=null;

    public PCBSolver(PCBProblem problem) {
        this.problem = problem;
    }

    public void solveWithEA(PCBProblem p)
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimplePermutationCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GuidedTwoOpt(problem.getData().getGuidedMap()));
        muList.add(new RandomRemoveReinsert());
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(p.getN()*p.getN()*p.getN());
        TerminalCondition tc2 = new CostBasedTC(0);
        List<TerminalCondition> tcList= new ArrayList<TerminalCondition>();
        tcList.add(tc1);
        tcList.add(tc2);
        TerminalCondition tc = new OrCompoundTC(tcList);


        EA alg = new EA(coList, muList, ps,tc);
        alg.addListener(new ConsoleShortMetaheuristicListener());


        alg.perform(p, new RandomPCBProblemSG());
    }

    public void solveWithTabuSearch(PCBProblem p)
    {
        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new MutationWrapperNF( new GuidedTwoOpt(problem.getData().getGuidedMap())));


        TerminalCondition tcIteration = new NeighboringBasedTC(p.getN()*p.getN()*p.getN());
        TerminalCondition tcCost  = new CostBasedTC(0);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcCost,tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,50);
        ts.addListener(new ConsoleShortMetaheuristicListener());

        ts.perform(p, new RandomPCBProblemSG());
    }

    public void solveWithSOS(PCBProblem p)
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimplePermutationCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GuidedTwoOpt(p.getData().getGuidedMap()));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(p.getN()*p.getN()*p.getN());
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1, tc2));

        SOS alg = new SOS(coList, muList, ps,tc);
        alg.addListener(new ConsolSOSListener());

        alg.perform(p,new RandomPCBProblemSG());
    }

    public void solveWithSA(PCBProblem p)
    {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new GuidedTwoOpt(problem.getData().getGuidedMap()));

        //muList.add(new SwapMutation());
        //muList.add(new RandomRemoveReinsert());

        SA alg = new SA(muList,new GeometricCooling(0.95),0,p.getN()*p.getN()*p.getN(),0);
        //alg.addListener(new ConsoleMetaheuristicListener());
        alg.addListener(new ConsoleShortMetaheuristicListener());

        alg.perform(p, new RandomPCBProblemSG());
    }

    public void solveWithDPSO(PCBProblem p)
    {
        DPSO dpso = new DPSO(0.6031,0.6485,0.6,95,new PCBProblemTC(p));
        dpso.addListener( new ConsoleShortMetaheuristicListener());

        dpso.perform(p, new RandomPCBProblemSG());
    }



    public void solveWithDDE(PCBProblem p)
    {
        DDE alg = new DDE(200,0.5,0.5,new NeighboringBasedTC(p.getN()*p.getN()*p.getN()),3);
        alg.addListener( new ConsoleShortMetaheuristicListener());

        alg.perform(p, new RandomPCBProblemSG());
    }


    public static void main(String args[])
    {
        PCBData pcbData = PCBData.constructPCBData("rPS11AK08-9.txt","./data/realPCB/rPS11AK08-9.txt");
        PCBProblem p = new PCBProblem(pcbData,0);

        PCBSolver ps = new PCBSolver(p);

        ps.run();
    }

    @Override
    public void run() {
        //solveWithEA(problem);
        //solveWithSA(problem);
        //solveWithDPSO(problem);
        //solveWithDDE(problem);
        //solveWithSOS(problem);
        //solveWithTabuSearch(problem);

        solveWithDPSO(problem);
    }
}
