package problems.bbob.f6_attractive_sector;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.DoubleVector;
import representation.base.Representation;

import java.util.Arrays;
import java.util.Random;


/**
 * This class implements the Attractive Sector function, a benchmark function
 * from the BBOB (Black-Box Optimization Benchmarking) test suite.
 *
 * The Attractive Sector function is defined as:
 *
 * f(x) = T_osz(sum_{i=1}^{D} (s_i * z_i)^2)^0.9 + f_opt
 *
 * where z_i = Q * Lambda^10 * R * (x - x_opt)
 * and s_i = 10^2 if z_i * x_i_opt > 0, otherwise s_i = 1.
 *
 * The function is highly asymmetric, with only one "hypercone" yielding low function values.
 * It is used to evaluate the performance of optimization algorithms on highly asymmetric landscapes.
 *
 * For more details, refer to the BBOB documentation:
 * https://coco.gforge.inria.fr/downloads/download16.00/bbobdocfunctions.pdf
 */
public class AttractiveSector extends AbstractBBOBProblem {


    double [][] R;
    double [][] Q;
    double[] lambda;

    public AttractiveSector(int dimension, Random rand) {
        super(dimension);

        R= BBOBTransformations.generateOrthogonalMatrix(dimension,rand);
        Q= BBOBTransformations.generateOrthogonalMatrix(dimension, rand);
        lambda = BBOBTransformations.createLambda(dimension,10);
    }

    @Override
    public double evaluate(double[] x) {
        double[] Rx = BBOBTransformations.multiply(R, x);
        BBOBTransformations.applyLambda(Rx, lambda);
        double[] z = BBOBTransformations.multiply(Q, Rx);

        double sum = 0.0;
        for (int i = 0; i < x.length; i++) {
            double penalty = (z[i] * x[i] > 0) ? 100.0 : 1.0;
            sum += penalty * z[i] * z[i];
        }
        return sum;
    }


    public static void main(String[] args) {
        int dimension = 10;
        double[] x = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            x[i] = (i % 2 == 0) ? i : -i; // Example input
        }

        Random rand = new Random(42); // Fixed seed for reproducibility

        AttractiveSector as = new AttractiveSector(dimension,rand);

        double result = as.cost(new DoubleVector(x));
        System.out.println(Arrays.toString(x));
        System.out.println("Attractive Sector Function value: " + result);
    }
}

