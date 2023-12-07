package problems.raps.neighboringFunction;

import base.NeighboringFunction;
import base.OptimizationProblem;
import problems.raps.Component;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import representation.SimpleIndividual;
import representation.base.Individual;
import util.ArrayUtil;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 7/7/2017.
 */
public class RAPCostWeightBalancingNF implements NeighboringFunction {
    private static final double ACCEPT_RATE = 0.3;
    private double BalanceThreshold= 0.5;

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {
        RAPS raps = (RAPS) problem;
        RAPSSolution rs = (RAPSSolution) i.getRepresentation().clone();
        tryImprove(raps,rs);
        double cost = raps.cost(rs);
        if ( i.getCost() > cost || RandUtil.rollDice(ACCEPT_RATE))
        {
            return new SimpleIndividual(rs,cost);
        }
        else return i.clone();


    }

    @Override
    public NeighboringFunction clone() {
        return new RAPCostWeightBalancingNF();
    }

    private void tryImprove(RAPS raps, RAPSSolution rs) {


        while(fixOneSubsystem(raps,rs))
        {
            int minReliable = ArrayUtil.getMinIndex(rs.getReliabilities());


            Component minComp = raps.getComponent(minReliable,rs.componentOf(minReliable));



            double costSlack = raps.getC()-rs.getSystemCost();
            double weightSlack = raps.getW()-rs.getSystemWeight();



            while ( (costSlack>minComp.getCost() && weightSlack>minComp.getWeight()))
            {
                rs.update(raps,minReliable,rs.componentOf(minReliable),rs.componentCountOf(minReliable)+1);
                costSlack = raps.getC()-rs.getSystemCost();
                weightSlack = raps.getW()-rs.getSystemWeight();
            }

        }
    }

    private boolean fixOneSubsystem(RAPS raps, RAPSSolution rs) {

        double costSlack = (raps.getC()-rs.getSystemCost())/raps.averageComponentCost();
        double weightSlack = (raps.getW()-rs.getSystemWeight())/raps.averageComponentWeight();

        if (costSlack>1 && weightSlack >1)
        {
            return false;
        }
        if (Math.abs(costSlack-weightSlack)<BalanceThreshold)
        {
            return false;
        }
        if (costSlack>weightSlack)
        {
            return reduceWeight(raps,rs);
        }
        else return reduceCost(raps,rs);
    }

    private boolean reduceCost(RAPS raps, RAPSSolution rs) {
        int costliest = raps.getCostliest(rs);
        int compIndex= rs.componentOf(costliest);
        Component c = raps.getComponent(costliest,compIndex);


        int compCount= raps.componentTypeCount(costliest);
        int ci = RandUtil.randInt(compCount);
        for (int i = 0; i <compCount ; i++) {
            int newCompIndex= (ci+i)%compCount;

            if (newCompIndex==compIndex)
                continue;
            Component nc = raps.getComponent(costliest,newCompIndex);
            if (nc.getCost()<c.getCost())
            {
                rs.update(raps,costliest,newCompIndex,rs.componentCountOf(costliest));
                return true;
            }
        }
        return false;
    }

    private boolean reduceWeight(RAPS raps, RAPSSolution rs) {
        int heaviestSubSystem = raps.getHeaviest(rs) ;
        int compIndex= rs.componentOf(heaviestSubSystem);
        Component c = raps.getComponent(heaviestSubSystem,compIndex);


        int compCount= raps.componentTypeCount(heaviestSubSystem);
        int ci = RandUtil.randInt(compCount);
        for (int i = 0; i <compCount ; i++) {
            int newCompIndex= (ci+i)%compCount;

            if (newCompIndex==compIndex)
                continue;
            Component nc = raps.getComponent(heaviestSubSystem,newCompIndex);
            if (nc.getWeight()<c.getWeight())
            {
                rs.update(raps,heaviestSubSystem,newCompIndex,rs.componentCountOf(heaviestSubSystem));
                return true;
            }
        }
        return false;
    }
}
