package base;

import problems.base.InitialSolutionGenerator;
import representation.IntegerPermutation;
import representation.base.Representation;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dindar.oz on 31.05.2017.
 */
public class RandomPermutationSG implements InitialSolutionGenerator {
    private final int size;

    public RandomPermutationSG(int size) {
        this.size = size;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {

        List<Representation> solutions = new ArrayList<>(c);

        List<Integer> solution = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            solution.add(i);
        }

        for (int i = 0; i < c; i++) {
            Collections.shuffle(solution);
            IntegerPermutation ip = new IntegerPermutation(solution);
            solutions.add(ip);
        }

        return solutions;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, RandUtil.getDefaultRNG(),c);
    }
}
