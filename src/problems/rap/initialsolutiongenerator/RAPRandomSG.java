package problems.rap.initialsolutiongenerator;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import problems.rap.RAP;
import problems.rap.RAPSolution;
import representation.base.Representation;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 7/4/2017.
 */
public class RAPRandomSG implements InitialSolutionGenerator {

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
        RAP rap = (RAP)problem;
        int subSystemCount = rap.subSystemCount();
        int system[][]= new int[subSystemCount][];

        for (int s = 0; s < subSystemCount; s++) {
            system[s] = new int[rap.componentTypeCount(s)];
            fillSubSystem(rap,rng,system,s);
        }

        RAPSolution rs = new RAPSolution(rap,system);

        return rs;
    }

    private void fillSubSystem(RAP rap, RNG rng, int[][] system, int s) {
        double costLimit = rap.getC()/rap.subSystemCount();
        double weightLimit = rap.getW()/rap.subSystemCount();
        int cost=0;
        int weight =0;

        while( cost<costLimit && weight<weightLimit)
        {
            int c = rng.randInt(system[s].length);
            system[s][c]++;
            cost+= rap.getComponent(s,c).getCost();
            weight+= rap.getComponent(s,c).getWeight();
        }
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, RandUtil.getDefaultRNG(),c);
    }
}
