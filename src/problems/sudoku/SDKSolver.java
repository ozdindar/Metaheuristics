package problems.sudoku;

import base.TerminalCondition;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.mutation.RandomInterchange;
import metaheuristic.ea.parentselector.CostBasedParentSelector;
import metaheuristic.ea.terminalcondition.CostBasedTC;
import metaheuristic.ea.terminalcondition.IterationBasedTC;
import metaheuristic.ea.terminalcondition.OrCompoundTC;
import metaheuristic.sa.GeometricCooling;
import metaheuristic.sa.SA;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class SDKSolver {
    int blockSize;
    int initialBoard[][];

    public SDKSolver(int blockSize, int[][] initialBoard) {
        this.blockSize = blockSize;
        this.initialBoard = initialBoard;
    }

    public void solveWithEA()
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new PlacementMutation());
        muList.add(new RemovalMutation());
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new IterationBasedTC(10000000);
        TerminalCondition tc2 = new CostBasedTC(0);
        List<TerminalCondition> tcList= new ArrayList<TerminalCondition>();
        tcList.add(tc1);
        tcList.add(tc2);
        TerminalCondition tc = new OrCompoundTC(tcList);

        EA alg = new EA(coList, muList, ps,tc);
        alg.setImmigrantCount(100);
        alg.setImmigrationPeriod(10000);

        SDKProblem p = new SDKProblem(blockSize,initialBoard);

        alg.perform(p, new RandomSDKProblemSG());
    }

    public void solveWithSA()
    {
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new RandomInterchange());

        SA alg = new SA( muList, new GeometricCooling(0.9),0.00001,10000,0);

        SDKProblem p = new SDKProblem(blockSize,initialBoard);
        alg.perform(p, new RandomSDKProblemSG());

    }


    public static void main(String args[])
    {
        int initialBoard[][] = new int[9][9];
        initialBoard[0][3] = 3;
        initialBoard[4][2] = 6;
        initialBoard[0][2] = 1;
        initialBoard[8][8] = 8;
        initialBoard[5][5] = 5;
        initialBoard[5][6] = 4;

        SDKRepresentation i = new SDKRepresentation(3,initialBoard);

        System.out.println(i);

        SDKSolver ns = new SDKSolver(3,initialBoard);
        ns.solveWithEA();
    }

}
