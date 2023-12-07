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
public class RAPRandomCountChangeMutation implements MutationOperator {
    private static final int MAX_ATTEMPT = 5;
    private static final int MAX_MUTATION_SIZE = 7;



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

    @Override
    public Representation apply(OptimizationProblem problem, Representation r) {
        RAPS rap = (RAPS) problem;

        RAPSSolution rs = (RAPSSolution) r.clone();

        int mutationSize = RandUtil.randInt(MAX_MUTATION_SIZE);
        for (int m = 0; m <mutationSize; m++) {
            int subSystem = RandUtil.randInt(rs.subSystemCount());

            if (rs.componentCountOf(subSystem)< 2) {
                rs.update(rap, subSystem, rs.componentOf(subSystem), rs.componentCountOf(subSystem) + 1);
                return rs;
            }
            if (rs.componentCountOf(subSystem)>= rap.getNmax()) {
                rs.update(rap, subSystem, rs.componentOf(subSystem), rs.componentCountOf(subSystem) - 1);
                return rs;
            }

            int delta = (RandUtil.rollDice(0.5))? 1:-1;
            rs.update(rap, subSystem, rs.componentOf(subSystem), rs.componentCountOf(subSystem) +delta);
        }

        repairSolution(rap,rs);

        return rs;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }
}
