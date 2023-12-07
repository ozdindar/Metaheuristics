package problems.mccdp.solutiongenerator;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import problems.mccdp.MCCDP;
import representation.IntegerVector;
import representation.base.Representation;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dindar.oz on 1.06.2017.
 */
public class RandomVectorSG implements InitialSolutionGenerator {
    List<Integer> values;
    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        MCCDP mccdp = (MCCDP)problem;
        values = new ArrayList<>(mccdp.targetPointCount());
        for (int i = 0; i < mccdp.targetPointCount(); i++) {
            values.add(i);
        }
        List<Representation> solutions = new ArrayList<>(c);


        for (int i = 0; i <c; i++) {
            Representation solution = generateASolution((MCCDP)problem,rng);
            solutions.add(solution);
        }

        return solutions;
    }

    private Representation generateASolution(MCCDP problem, RNG rng) {
        int n = 1 + rng.randInt(problem.targetPointCount());

        Collections.shuffle(values);

        IntegerVector solution = problem.getWsnModel().firstFit(values);
        return solution;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, RandUtil.getDefaultRNG(),c);
    }
}
