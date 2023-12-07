package problems.nqueen;

import base.MutationWrapperNF;
import base.NeighboringFunction;
import base.TerminalCondition;
import metaheuristic.ConsoleMetaheuristicListener;
import metaheuristic.dpso.DPSO;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.crossover.SimplePermutationCrossover;
import metaheuristic.ea.mutation.AdjacentInterchange;
import metaheuristic.ea.mutation.RandomInterchange;
import metaheuristic.ea.mutation.RandomRemoveReinsert;
import metaheuristic.ea.mutation.ReverseSubSequence;
import metaheuristic.ea.parentselector.CostBasedParentSelector;
import metaheuristic.ea.terminalcondition.CostBasedTC;
import metaheuristic.ea.terminalcondition.IterationBasedTC;
import metaheuristic.ea.terminalcondition.NeighboringBasedTC;
import metaheuristic.ea.terminalcondition.OrCompoundTC;
import metaheuristic.hbmo.HBMO;
import metaheuristic.sa.GeometricCooling;
import metaheuristic.sa.SA;
import metaheuristic.sos.SOS;
import metaheuristic.tabu.SolutionMemoryTabuList;
import metaheuristic.tabu.TabuSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class NQSolver {
    int n;

    public NQSolver(int n) {
        this.n = n;
    }

    public void solveWithEA()
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimplePermutationCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RandomInterchange());
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new IterationBasedTC(100000);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        EA alg = new EA(coList, muList, ps,tc);
        alg.addListener(new ConsoleMetaheuristicListener());

        NQProblem p = new NQProblem(n);


        alg.perform(p, new RandomNQueenSG());
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

        NQProblem p = new NQProblem(n);

        alg.perform(p, new RandomNQueenSG());
    }

    public void solveWithSA()
    {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RandomInterchange());

        SA alg = new SA(muList, new GeometricCooling(0.9),0.00001,1000,0);

        NQProblem p = new NQProblem(n);

        alg.perform(p, new RandomNQueenSG());
    }

    public void solveWithTabuSearch()
    {
        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new MutationWrapperNF(new RandomInterchange()));


        TerminalCondition tcIteration = new IterationBasedTC(1000000);
        TerminalCondition tcCost  = new CostBasedTC(0);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcCost,tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,50);
        ts.addListener(new ConsoleMetaheuristicListener());

        NQProblem p = new NQProblem(n);

        ts.perform(p, new RandomNQueenSG());
    }

    public void solveWithDPSO()
    {
        NQProblem p = new NQProblem(n);

        DPSO dpso = new DPSO(0.6031,0.6485,0.6475,95, new NeighboringBasedTC(10000) );



        dpso.perform(p, new RandomNQueenSG());
    }

    public void solveWithHBMO()
    {
        NQProblem p = new NQProblem(n);

        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RandomInterchange());
        muList.add(new RandomRemoveReinsert());
        muList.add(new ReverseSubSequence());
        muList.add(new AdjacentInterchange());
        muList.add(new NQSwapMutation());

        TerminalCondition tc1 = new NeighboringBasedTC(1000000);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1,tc2));

        HBMO hbmo = new HBMO(muList,tc,new SimplePermutationCrossover(),30,10,3,0.1);
        hbmo.addListener(new ConsoleMetaheuristicListener());
        hbmo.perform(p, new RandomNQueenSG());
    }

    public static void main(String args[])
    {
        NQSolver ns = new NQSolver(200);
        ns.solveWithTabuSearch();
        //ns.solveWithHBMO();
        //ns.solveWithEA();
        //ns.solveWithSA();
    }

}
