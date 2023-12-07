package search.example;

import representation.IntegerPermutation;
import representation.base.Representation;
import search.SearchProblem;
import search.astar.SearchHeuristic;

/**
 * Created by dindar.oz on 04.06.2015.
 */
public class NBlockMHDHeuristic implements SearchHeuristic {
    @Override
    public double estimate(SearchProblem p,Representation state) {
        NBlockProblem np = (NBlockProblem)p;
        IntegerPermutation is =(IntegerPermutation)state;

        int h =0;

        for (int i=0;i<is.getLength();i++)
        {
            int x = np.boardIndexX(i);
            int y = np.boardIndexY(i);

            int ox = np.boardIndexX(is.get(i));
            int oy = np.boardIndexY(is.get(i));

            h += Math.abs(x-ox);
            h += Math.abs(y-oy);
        }

        return h;
    }
}
