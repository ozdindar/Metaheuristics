package problems.raps.neighboringFunction;

import base.NeighboringFunction;
import base.OptimizationProblem;
import problems.raps.Component;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import representation.SimpleIndividual;
import representation.base.Individual;
import util.ArrayUtil;

/**
 * Created by dindar.oz on 7/7/2017.
 */
public class RAPReliabilityBalancingNF implements NeighboringFunction {
    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {
        RAPS raps = (RAPS) problem;
        RAPSSolution rs = (RAPSSolution) i.getRepresentation().clone();

        if (tryImprove(raps,rs))
        {
            return new SimpleIndividual(rs,raps.cost(rs));
        }
        else return i.clone();


    }

    @Override
    public NeighboringFunction clone() {
        return new RAPReliabilityBalancingNF();
    }

    private boolean tryImprove(RAPS raps, RAPSSolution rs) {

        boolean improved = false;

        while(true)
        {
            int maxReliable =ArrayUtil.getMaxIndex(rs.getReliabilities());
            int minReliable =ArrayUtil.getMinIndex(rs.getReliabilities());

            Component maxComp = raps.getComponent(maxReliable,rs.componentOf(maxReliable));
            Component minComp = raps.getComponent(minReliable,rs.componentOf(minReliable));

            int maxCompCount = rs.componentCountOf(maxReliable);

            double costSlack = raps.getC()-rs.getSystemCost();
            double weightSlack = raps.getW()-rs.getSystemWeight();

            double oldR= rs.getSystemR();

            while ( (costSlack<minComp.getCost() || weightSlack<minComp.getWeight()) &&
                    rs.componentCountOf(maxReliable)>1)
            {
                rs.update(raps,maxReliable,rs.componentOf(maxReliable),rs.componentCountOf(maxReliable)-1);
                costSlack = raps.getC()-rs.getSystemCost();
                weightSlack = raps.getW()-rs.getSystemWeight();
            }
            if ((costSlack>minComp.getCost() && weightSlack>minComp.getWeight()))
            {
                rs.update(raps,minReliable,rs.componentOf(minReliable),rs.componentCountOf(minReliable)+1);
                if (rs.getSystemR()<=oldR)
                {
                    rs.update(raps,maxReliable,rs.componentOf(maxReliable),maxCompCount);
                    rs.update(raps,minReliable,rs.componentOf(minReliable),rs.componentCountOf(minReliable)-1);
                    return improved;
                }
                else{
                    improved=true;
                    maxCompCount = rs.componentCountOf(maxReliable);
                }
            }
            else return improved;
        }
    }
}
