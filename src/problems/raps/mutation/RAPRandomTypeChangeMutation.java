package problems.raps.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import representation.base.Representation;
import util.ArrayUtil;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPRandomTypeChangeMutation implements MutationOperator {
    private static final int MAX_ATTEMPT = 5;

    private static final int MAX_MUTATION_SIZE = 7;

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        RAPS rap = (RAPS) problem;

        RAPSSolution rs = (RAPSSolution) r.clone();

        int mutationSize = RandUtil.randInt(MAX_MUTATION_SIZE);

        for (int m = 0; m < mutationSize; m++) {
            int subSystem = RandUtil.randInt(rs.subSystemCount());

            int component= rs.componentOf(subSystem);
            while (component==rs.componentOf(subSystem))
                component= RandUtil.randInt(rap.componentTypeCount(subSystem));

            rs.update(rap, subSystem, component, rs.componentCountOf(subSystem));
        }


        repairSolution(rap,rs);
        return rs;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private void repairSolution(RAPS rap, RAPSSolution rs) {
        double costSlack = rap.getC()-rs.getSystemCost();
        double weightSlack = rap.getW()-rs.getSystemWeight();

        while (costSlack<0 || weightSlack<0)
        {
            int maxReliable = ArrayUtil.getMaxIndex(rs.getReliabilities());
            rs.update(rap,maxReliable,rs.componentOf(maxReliable),rs.componentCountOf(maxReliable)-1);
            costSlack = rap.getC()-rs.getSystemCost();
            weightSlack = rap.getW()-rs.getSystemWeight();
        }
    }
}
