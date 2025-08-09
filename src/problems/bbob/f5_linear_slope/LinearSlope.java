package problems.bbob.f5_linear_slope;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.f4_skew_rastrigin.SkewRastrigin;
import representation.DoubleVector;
import representation.base.Representation;




/**
 * This class implements the Linear Slope function, a benchmark function
 * from the BBOB (Black-Box Optimization Benchmarking) test suite.
 *
 * F5: The Linear Slope function is defined as:
 *
 * f(x) = sum_{i=1}^{D} (5 * |s_i| - s_i * z_i) + f_opt
 *
 * where z_i = x_i if x_i < x_i^opt, otherwise z_i = x_i^opt.
 *
 * The function is linear and is used to evaluate the performance of optimization algorithms
 * on simple, yet non-trivial problems.
 *
 * For more details, refer to the BBOB documentation:
 * https://coco.gforge.inria.fr/downloads/download16.00/bbobdocfunctions.pdf
 */

public class LinearSlope extends AbstractBBOBProblem {


    public LinearSlope(int dimension) {
        super(dimension);
    }

    @Override
    public double evaluate(double[] values) {
        double sum = 0;
        double f_opt = 0; // Optimal function value, if any
        for (int j = 0; j < values.length; j++) {
            double s_i = Math.signum(values[j]) * Math.pow(10, (j / (dimension - 1.0)));
            double z_i = values[j] < 5 ? values[j] : 5;
            sum += 5 * Math.abs(s_i) - s_i * z_i;
        }
        return sum + f_opt;
    }


    public static void main(String[] args) {
        LinearSlope ls = new LinearSlope(3);
        double[] input = {0.0, 0.0, 0.0}; // Global minimum
        double[] input2 = {5.0, 5.0, -5.0};
        double cost = ls.cost(new DoubleVector(input2));
        System.out.println("Linear Slope BBOB cost: " + cost);
    }
}
