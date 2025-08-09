package problems.raps.initialsolutiongenerator;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import problems.raps.Component;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import representation.base.Representation;
import util.ArrayUtil;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 7/4/2017.
 */
public class RAPSRandomSG implements InitialSolutionGenerator {

    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        List<Representation> solutions = new ArrayList<>();

        for (int i = 0; i < c; i++) {
            Representation r = generateOne(problem,rng);
            solutions.add(r);
        }
        return solutions;
    }

    private Representation generateOne(OptimizationProblem problem, RNG rng) {
        RAPS rap = (RAPS)problem;
        int subSystemCount = rap.subSystemCount();
        int system[][]= new int[subSystemCount][2];

        for (int s = 0; s < subSystemCount; s++) {
            fillSubSystem(rap,rng,system,s);
        }

        RAPSSolution rs = new RAPSSolution(rap,system);

        fillSystem(rap,rs);

        return rs;
    }

    private void fillSystem(RAPS rap, RAPSSolution rs) {
        while(true)
        {
            int maxReliable = ArrayUtil.getMaxIndex(rs.getReliabilities());
            int minReliable =ArrayUtil.getMinIndex(rs.getReliabilities());

            Component maxComp = rap.getComponent(maxReliable,rs.componentOf(maxReliable));
            Component minComp = rap.getComponent(minReliable,rs.componentOf(minReliable));

            int maxCompCount = rs.componentCountOf(maxReliable);

            double costSlack = rap.getC()-rs.getSystemCost();
            double weightSlack = rap.getW()-rs.getSystemWeight();

            double oldR= rs.getSystemR();

            while ( (costSlack<minComp.getCost() || weightSlack<minComp.getWeight()) &&
                    rs.componentCountOf(maxReliable)>1)
            {
                rs.update(rap,maxReliable,rs.componentOf(maxReliable),rs.componentCountOf(maxReliable)-1);
                costSlack = rap.getC()-rs.getSystemCost();
                weightSlack = rap.getW()-rs.getSystemWeight();
            }
            if ((costSlack>minComp.getCost() && weightSlack>minComp.getWeight()))
            {
                rs.update(rap,minReliable,rs.componentOf(minReliable),rs.componentCountOf(minReliable)+1);
                if (rs.getSystemR()<=oldR)
                {
                    rs.update(rap,maxReliable,rs.componentOf(maxReliable),maxCompCount);
                    rs.update(rap,minReliable,rs.componentOf(minReliable),rs.componentCountOf(minReliable)-1);
                    return ;
                }
                else{
                    maxCompCount = rs.componentCountOf(maxReliable);
                }
            }
            else return ;
        }
    }

    private void fillSubSystem(RAPS rap, RNG rng, int[][] system, int s) {
        double costLimit = rap.getC()/rap.subSystemCount();
        double weightLimit = rap.getW()/rap.subSystemCount();
        int cost=0;
        int weight =0;

        int component = rng.randInt(rap.componentTypeCount(s));
        int c1 = (int) (costLimit/rap.getComponent(s,component).getCost());
        int c2 = (int) (weightLimit/rap.getComponent(s,component).getWeight());
        system[s][0]= component;
        system[s][1]=  Math.min(c1,c2);
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, RandUtil.getDefaultRNG(),c);
    }
}
