package problems.rap.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.rap.RAP;
import problems.rap.RAPSolution;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPRandomRemoveMutation implements MutationOperator {
    private static final int MAX_ATTEMPT = 5;

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        RAP rap = (RAP) problem;

        RAPSolution rs = (RAPSolution) r.clone();
        int subSystem = RandUtil.randInt(rs.subSystemCount());

        int attempt =0;
        while (rs.componentCountOf(subSystem)<2 && attempt++<MAX_ATTEMPT) {
            subSystem = RandUtil.randInt(rs.subSystemCount());
        }

        if (attempt>MAX_ATTEMPT)
            return rs;

        attempt =0;
        int component = RandUtil.randInt(rap.componentTypeCount(subSystem));
        while (rs.get(subSystem,component)<=0 && attempt++<MAX_ATTEMPT) {
            component = RandUtil.randInt(rap.componentTypeCount(subSystem));
        }
        if (attempt>MAX_ATTEMPT)
            return rs;

        int newCount= rs.get(subSystem,component)-1;

        rs.update(rap,subSystem,component,newCount);
        return rs;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }
}
