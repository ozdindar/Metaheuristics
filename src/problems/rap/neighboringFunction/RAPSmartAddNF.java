package problems.rap.neighboringFunction;

import base.NeighboringFunction;
import base.OptimizationProblem;
import problems.rap.Component;
import problems.rap.RAP;
import problems.rap.RAPSolution;
import representation.SimpleIndividual;
import representation.base.Individual;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPSmartAddNF implements NeighboringFunction {

    private static final int MAX_ATTEMPT = 5;

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {
        RAP rap = (RAP) problem;

        RAPSolution rs = (RAPSolution) i.getRepresentation().clone();
        int subSystem = RandUtil.randInt(rs.subSystemCount());

        double costDensity = (rap.getC()-rs.getSystemCost())/rap.averageComponentCost();
        if (costDensity<1) {
            removeCostly(rap, rs);
            return new SimpleIndividual(rs,rap.cost(rs));
        }
        double weightDensity = (rap.getW()-rs.getSystemWeight())/rap.averageComponentWeight();
        if (weightDensity<1) {
            removeHeavy(rap, rs);
            return new SimpleIndividual(rs,rap.cost(rs));
        }


        int attempt=0;
        while (rs.componentCountOf(subSystem)>=rap.getNmax()&& attempt++ < MAX_ATTEMPT) {
            subSystem = RandUtil.randInt(rs.subSystemCount());
        }

        if (attempt>MAX_ATTEMPT)
            return new SimpleIndividual(rs,i.getCost());

        int component = RandUtil.randInt(rap.componentTypeCount(subSystem));
        int newCount= rs.get(subSystem,component)+1;



        rs.update(rap,subSystem,component,newCount);
        double cost= rap.cost(rs);

        if (cost<0)
        {
            double c= rap.cost(rs);
        }

        return new SimpleIndividual(rs,cost);
    }

    @Override
    public NeighboringFunction clone() {
        return new RAPSmartAddNF();
    }

    private void removeCostly(RAP rap, RAPSolution rs) {
        int maxCC= -1;
        int maxS =-1;
        int maxC =-1;
        int maxCost = -1;
        for (int s = 0; s < rs.subSystemCount(); s++) {
            if (rs.componentCountOf(s)==0)
                continue;
            for (int c = 0; c < rs.getSystem()[s].length; c++) {

                int cc= rs.get(s,c);
                if (cc==0)
                    continue;
                Component component= rap.getComponent(s,c);
                if (component.getCost()>maxCost)
                {
                    maxS=s;
                    maxC=c;
                    maxCC = cc;
                    maxCost= (int) component.getCost();
                }
            }
        }
        if (maxCost>=0)
            rs.update(rap,maxS,maxC,maxCC-1);
    }

    private void removeHeavy(RAP rap, RAPSolution rs) {
        int maxCC= -1;
        int maxS =-1;
        int maxC =-1;
        int maxWeight = -1;
        for (int s = 0; s < rs.subSystemCount(); s++) {
            if (rs.componentCountOf(s)<2)
                continue;
            for (int c = 0; c < rs.getSystem()[s].length; c++) {

                int cc= rs.get(s,c);
                if (cc==0)
                    continue;
                Component component= rap.getComponent(s,c);
                if (component.getWeight()>maxWeight)
                {
                    maxS=s;
                    maxC=c;
                    maxCC = cc;
                    maxWeight = (int) component.getWeight();
                }
            }
        }
        if (maxWeight>=0)
            rs.update(rap,maxS,maxC,maxCC-1);
    }
}
