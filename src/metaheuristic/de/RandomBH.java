package metaheuristic.de;

import metaheuristic.pso.base.ContinousProblem;

import java.security.SecureRandom;
import java.util.Random;

public class RandomBH implements BoundaryHandler{
    Random rng = new SecureRandom();
    @Override
    public void handle(ContinousProblem cProblem, double[] x, double[] target) {
        for (int d = 0; d < x.length; d++) {
            double lower = cProblem.getLowerBound(d);
            double upper = cProblem.getUpperBound(d);
            if (x[d] < lower || x[d]>upper ) {
                x[d] = lower + rng.nextDouble()*(upper-lower);
            }
        }
    }
}
