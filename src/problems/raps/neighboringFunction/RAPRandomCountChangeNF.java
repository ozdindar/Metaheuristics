package problems.raps.neighboringFunction;

import base.NeighboringFunction;
import base.OptimizationProblem;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import representation.SimpleIndividual;
import representation.base.Individual;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPRandomCountChangeNF implements NeighboringFunction {
    private static final int MAX_ATTEMPT = 5;

    @Override
    public Individual apply(OptimizationProblem problem, Individual r) {
        RAPS rap = (RAPS) problem;

        RAPSSolution rs = (RAPSSolution) r.getRepresentation().clone();
        int subSystem = RandUtil.randInt(rs.subSystemCount());

        if (rs.componentCountOf(subSystem)< 2) {
            rs.update(rap, subSystem, rs.componentOf(subSystem), rs.componentCountOf(subSystem) + 1);
            return new SimpleIndividual(rs,rap.cost(rs));
        }
        if (rs.componentCountOf(subSystem)>= rap.getNmax()) {
            rs.update(rap, subSystem, rs.componentOf(subSystem), rs.componentCountOf(subSystem) - 1);
            return new SimpleIndividual(rs,rap.cost(rs));
        }

        int delta = (RandUtil.rollDice(0.5))? 1:-1;
        rs.update(rap, subSystem, rs.componentOf(subSystem), rs.componentCountOf(subSystem) +delta);

        return new SimpleIndividual(rs,rap.cost(rs));
    }

    @Override
    public NeighboringFunction clone() {
        return new RAPRandomCountChangeNF();
    }
}
