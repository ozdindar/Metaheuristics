package problems.toptw.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.toptw.representation.TourList;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class ExternalSwap implements MutationOperator {
    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
        TourList tl = (TourList) i;
        TourList ntl = (TourList) tl.clone();

        int tour1 = RandUtil.randInt(tl.tourCount());
        int tour2 = RandUtil.randInt(tl.tourCount());

        if (tl.get(tour1).isEmpty() || tl.get(tour2).isEmpty())
            return ntl;
        int n1 = RandUtil.randInt(tl.get(tour1).size());
        int n2 = RandUtil.randInt(tl.get(tour2).size());
        ntl.externalSwap(tour1,n1,tour2,n2);
        return ntl;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }
}
