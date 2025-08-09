package problems.bbob.f9_rosenbrock_rotated;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.BBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.DoubleVector;
import representation.base.Representation;

import java.security.SecureRandom;
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
public class Rosenbrock_R extends AbstractBBOBProblem {


    double[] xOpt;
    double[][] R;


    public Rosenbrock_R(int dimension, Random rnd) {
        super(dimension);

        xOpt = new double[dimension];
        Arrays.fill(xOpt,0.0);

        R = BBOBTransformations.generateOrthogonalMatrix(dimension,rnd);


    }



    @Override
    public double evaluate(double[] x) {
        int D = x.length;
        double scaling = Math.max(1.0, Math.sqrt(D) / 8.0);
        double[] z = new double[D];


        for (int i = 0; i < D; i++) {
            x[i] = (x[i] - xOpt[i]) ;
        }

        double[] Rx = BBOBTransformations.multiply(R,x);

        for (int i = 0; i < D; i++) {
            z[i] = scaling * Rx[i] + 0.5;
        }

        // Compute the Rosenbrock function
        double sum = 0.0;
        for (int i = 0; i < D - 1; i++) {
            double zi = z[i];
            double zip1 = z[i + 1];
            double term1 = 100.0 * Math.pow(zi * zi - zip1, 2);
            double term2 = Math.pow(zi - 1.0, 2);
            sum += term1 + term2;
        }

        return sum + 0;
    }


    public static void main(String[] args) {
        int dimension = 10;
        double[] x = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            x[i] = (i % 2 == 0) ? i : -i; // Example input
        }

        Random rand = new Random(42); // Fixed seed for reproducibility

        Rosenbrock_R as = new Rosenbrock_R(dimension, new SecureRandom());

        double result = as.cost(new DoubleVector(x));
        System.out.println(Arrays.toString(x));
        System.out.println("Rosenbrock Function value: " + result);
    }
}

