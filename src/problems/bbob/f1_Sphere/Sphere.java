package problems.bbob.f1_Sphere;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.BBOBProblem;
import representation.DoubleVector;
import representation.base.Representation;


/**
 * This class implements the Sphere function, a fundamental benchmark function
 * from the BBOB (Black-Box Optimization Benchmarking) test suite.
 *
 * The Sphere function is defined as:
 *
 * f(x) = sum_{i=1}^{n} x_i^2
 *
 * where x is an n-dimensional vector.
 *
 * The function is unimodal and convex, making it one of the simplest optimization problems.
 * It is commonly used to evaluate the performance of optimization algorithms.
 *
 * For more details, refer to the BBOB documentation:
 * https://coco.gforge.inria.fr/downloads/download16.00/bbobdocfunctions.pdf
 */

public class Sphere extends AbstractBBOBProblem {

    public Sphere(int dimension) {
        super(dimension);
    }

    @Override
    public double evaluate(double[] x) {
        double sum = 0;
        for (double value : x) {
            sum += value * value;
        }
        return sum;
    }


}
