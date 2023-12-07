package problems.raps.crossover;

import base.OptimizationProblem;
import metaheuristic.ea.base.CrossOverOperator;
import problems.raps.RAPS;
import problems.raps.RAPSSolution;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 7/7/2017.
 */
public class RAPSimpleCrossover implements CrossOverOperator {
    @Override
    public List<Representation> apply(OptimizationProblem problem, Representation p1, Representation p2) {
        List<Representation> offsprings = new ArrayList<>(2);

        RAPS raps = (RAPS)problem;
        RAPSSolution prs1= (RAPSSolution)p1;
        RAPSSolution prs2= (RAPSSolution)p2;
        int system1[][] = new int[prs1.subSystemCount()][2];
        int system2[][] = new int[prs1.subSystemCount()][2];
        for (int s = 0; s < system1.length; s++) {
            system1[s][0] = (s<system1.length/2) ? prs1.componentOf(s):prs2.componentOf(s);
            system1[s][1] = (s<system1.length/2) ? prs1.componentCountOf(s):prs2.componentCountOf(s);

            system2[s][0] = (s<system1.length/2) ? prs2.componentOf(s):prs1.componentOf(s);
            system2[s][1] = (s<system1.length/2) ? prs2.componentCountOf(s):prs1.componentCountOf(s);
        }
        RAPSSolution offspring1 = new RAPSSolution(raps,system1);
        RAPSSolution offspring2 = new RAPSSolution(raps,system1);
        offsprings.addAll(Arrays.asList(offspring1,offspring2));
        return offsprings;
    }
}
