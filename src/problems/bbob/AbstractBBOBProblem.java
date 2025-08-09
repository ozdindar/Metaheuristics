package problems.bbob;

import base.OptimizationProblem;
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

public abstract class AbstractBBOBProblem implements OptimizationProblem, BBOBProblem {

    protected int dimension;

    public AbstractBBOBProblem(int dimension) {
        this.dimension = dimension;
    }


    public int getDimension() {
        return dimension;
    }

    @Override
    public boolean isFeasible(Representation i) {
        // For the Sphere function, all points are feasible
        return true;
    }

    @Override
    public abstract double evaluate(double[] x);

    @Override
    public double cost(Representation i) {
        DoubleVector dv = (DoubleVector) i;
        double[] values = dv.getValues();

        return evaluate(values);
    }

    @Override
    public double maxDistance() {
        // Maximum distance in the search space [-5, 5]^n
        return Math.sqrt(dimension * 25);
    }
}
