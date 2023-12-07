package problems.dejong;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.DoubleVector;
import representation.base.Representation;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 15.12.2016.
 */
public class RandomDejongF1SG implements InitialSolutionGenerator {
    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        return generate(problem,c);
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        DejongF1 dp = (DejongF1)problem;
        List<Representation> individuals = new ArrayList<>();
        for (int i=0;i<c;i++)
        {
            double values[] = RandUtil.randDoubles(dp.n,DejongF1.MIN_X,DejongF1.MAX_X);
            individuals.add(new DoubleVector(values));
        }

        return individuals;
    }
}
