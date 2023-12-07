package problems.movingpeaks;

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
public class RandomMovingPeaksSG implements InitialSolutionGenerator {
    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        return generate(problem,c);
    }

    private Representation generateRandomState(MovingPeaks mp) {
        DoubleVector v = new DoubleVector(MP_Scenario.geno_size);

        for (int d = 0 ;d<v.getValues().length;d++)
        {
            v.getValues()[d] = RandUtil.randDouble(mp.scenario.getMincoordinate(),mp.scenario.getMaxcoordinate());
        }

        return v;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        MovingPeaks mp = (MovingPeaks) problem;

        mp.init_peaks();
        List<Representation> initials = new ArrayList<>();

        for (int i=0;i<c ;i++)
        {
            initials.add(generateRandomState(mp));
        }

        return initials;
    }
}
