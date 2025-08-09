package problems.rap.neighboringFunction;

import base.NeighboringFunction;
import base.OptimizationProblem;
import problems.rap.RAP;
import problems.rap.RAPSolution;
import representation.SimpleIndividual;
import representation.base.Individual;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPRandomRemoveNF implements NeighboringFunction {
    private static final int MAX_ATTEMPT = 5;

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {
        RAP rap = (RAP) problem;

        RAPSolution rs = (RAPSolution) i.getRepresentation().clone();
        int subSystem = RandUtil.randInt(rs.subSystemCount());

        int attempt =0;
        while (rs.componentCountOf(subSystem)<2 && attempt++<MAX_ATTEMPT) {
            subSystem = RandUtil.randInt(rs.subSystemCount());
        }

        if (attempt>MAX_ATTEMPT)
            return new SimpleIndividual(rs,i.getCost());

        attempt =0;
        int component = RandUtil.randInt(rap.componentTypeCount(subSystem));
        while (rs.get(subSystem,component)<=0 && attempt++<MAX_ATTEMPT) {
            component = RandUtil.randInt(rap.componentTypeCount(subSystem));
        }
        if (attempt>MAX_ATTEMPT)
            return new SimpleIndividual(rs,i.getCost());

        int newCount= rs.get(subSystem,component)-1;

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
        return new RAPRandomRemoveNF();
    }
}
