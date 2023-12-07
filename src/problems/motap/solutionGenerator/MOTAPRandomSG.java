package problems.motap.solutionGenerator;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Representation;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 15.12.2016.
 */
public class MOTAPRandomSG implements InitialSolutionGenerator {
    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        List<Representation> states = new ArrayList<>();

        for (int i=0;i<c ;i++)
        {
            states.add(generateRandom((MOTAProblem) problem, rng));
        }

        return states;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem,RandUtil.getDefaultRNG(),c);
    }


    private Representation generateRandom(MOTAProblem problem, RNG rng) {
        int[] values = new int[problem.getTask().getModules().size()] ;
        for (int i=0;i<values.length;i++)
        {
            values[i] = rng.randInt(problem.getDcs().getProcessors().size());
        }

        Representation state = new IntegerAssignment(values);
        return state;
    }
}
