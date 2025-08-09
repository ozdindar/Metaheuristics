package problems.bbob.f3_rastrigin_sep;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.DoubleVector;
import representation.base.Representation;

import java.util.Arrays;


/**
 * This class implements the Rastrigin Separable function, a well-known benchmark function
 * from the BBOB (Black-Box Optimization Benchmarking) test suite.
 *
 * The Rastrigin function is defined as:
 *
 * f(x) = 10n + sum_{i=1}^{n} [ x_i^2 - 10 * cos(2 * pi * x_i) ]
 *
 * where x is an n-dimensional vector.
 *
 * The function is highly multimodal, making it a challenging optimization problem.
 * It is commonly used to evaluate the performance of optimization algorithms.
 *
 * For more details, refer to the BBOB documentation:
 * https://coco.gforge.inria.fr/downloads/download16.00/bbobdocfunctions.pdf
 */

public class RastriginSeparable extends AbstractBBOBProblem {



    public RastriginSeparable(int dimension) {
        super(dimension);
    }


    private static double[] transform(double[] x, double beta, double alpha) {
        double[] z = Arrays.stream(x).map(BBOBTransformations::tOsz).toArray();
        z = BBOBTransformations.tAsy(z, beta);
        z = BBOBTransformations.applyLambda(z, alpha);
        return z;
    }

    @Override
    public double evaluate(double[] values) {
        double beta = 0.2;
        double alpha = 10.0;
        double[] z= transform(values,beta,alpha);
        double sum = 10 * dimension;
        for (double value : z) {
            sum += value * value - 10 * Math.cos(2 * Math.PI * value);
        }
        return sum;
    }


    public static void main(String[] args) {
        RastriginSeparable rs= new RastriginSeparable(3);

        double[] input = {0.0, 0.0, 0.0}; // Global minimum
        double cost = rs.cost(new DoubleVector(input));
        System.out.println("Rastrigin Separated BBOB cost: " + cost);
    }
}
