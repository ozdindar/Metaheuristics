package problems.bbob;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;

import representation.DoubleVector;
import representation.base.Representation;
import util.random.RNG;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BBOBRandomISG implements InitialSolutionGenerator {

    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        List<Representation> initialStates = new ArrayList<>();

        for (int i = 0; i < c; i++) {
            double[] values = new double[problem.getDimension()];
            for (int j = 0; j < values.length; j++) {
                values[j] = rng.randDouble() * 10 - 5; // Random values between -5 and 5
            }
            initialStates.add(new DoubleVector(values));
        }
        return initialStates;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        List<Representation> initialStates = new ArrayList<>();

        Random rng = new Random();
        for (int i = 0; i < c; i++) {
            double[] values = new double[problem.getDimension()];
            for (int j = 0; j < values.length; j++) {
                values[j] = rng.nextDouble() * 10 - 5; // Random values between -5 and 5
            }
            initialStates.add(new DoubleVector(values));
        }
        return initialStates;
    }
}
