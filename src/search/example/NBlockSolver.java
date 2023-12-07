package search.example;

import representation.IntegerPermutation;
import search.SearchAlgorithm;
import search.SearchNode;
import search.astar.AstarSearchAlgorithm;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public class NBlockSolver
{
    public static void main(String args[])
    {
        IntegerPermutation initialState = new IntegerPermutation(new Integer[]{2,1,4,5,3,0,6,8,7});
        IntegerPermutation targetState  = new IntegerPermutation(new Integer[]{0,1,2,3,4,5,6,7,8});

        NBlockProblem np = new NBlockProblem(initialState,targetState);


        SearchAlgorithm alg = new AstarSearchAlgorithm(new NBlockMHDHeuristic());

        SearchNode solution = alg.solve(np);
        System.out.println(np.printSolution(solution));

    }
}
