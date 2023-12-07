package problems.raps.neighboringFunction;

import base.NeighboringFunction;
import base.OptimizationProblem;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import representation.SimpleIndividual;
import representation.base.Individual;
import util.ArrayUtil;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPRandomTypeChangeNF implements NeighboringFunction {
    private static final int MAX_ATTEMPT = 5;

    @Override
    public Individual apply(OptimizationProblem problem, Individual r) {
        RAPS rap = (RAPS) problem;

        RAPSSolution rs = (RAPSSolution) r.getRepresentation().clone();
        int subSystem = RandUtil.randInt(rs.subSystemCount());

        int component= rs.componentOf(subSystem);
        while (component==rs.componentOf(subSystem))
            component= RandUtil.randInt(rap.componentTypeCount(subSystem));


        rs.update(rap, subSystem, component, rs.componentCountOf(subSystem));

        repairSolution(rap,rs);

        return new SimpleIndividual(rs,rap.cost(rs));
    }

    @Override
    public NeighboringFunction clone() {
        return new RAPRandomTypeChangeNF();
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
