package metaheuristic.de;

import metaheuristic.pso.base.ContinousProblem;
import representation.DoubleVector;

public class SaturationBH implements BoundaryHandler{
    @Override
    public void handle(ContinousProblem cProblem, double[] x, double[] target) {
        for (int d = 0; d < x.length; d++) {
            double lower = cProblem.getLowerBound(d);
            double upper = cProblem.getUpperBound(d);
            if (x[d]<lower || x[d]>upper) {
                boolean aha = true;
            }
            x[d] = Math.max(lower, Math.min(upper,x[d]));
        }
    }
}
