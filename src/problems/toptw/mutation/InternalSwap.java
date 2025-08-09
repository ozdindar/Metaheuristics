package problems.toptw.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.toptw.representation.TourList;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class InternalSwap implements MutationOperator {
    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
        TourList tl = (TourList) i;
        TourList ntl = (TourList) tl.clone();

        int tour = RandUtil.randInt(tl.tourCount());

        if (tl.get(tour).isEmpty())
            return ntl;

        int n1 = RandUtil.randInt(tl.get(tour).size());
        int n2 = RandUtil.randInt(tl.get(tour).size());
        ntl.internalSwap(tour,n1,n2);
        return ntl;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }
}
