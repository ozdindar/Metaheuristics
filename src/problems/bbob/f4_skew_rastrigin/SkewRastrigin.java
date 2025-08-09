package problems.bbob.f4_skew_rastrigin;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.DoubleVector;
import representation.base.Representation;



/**
 * This class implements the Skew Rastrigin-Bueche function, a benchmark function
 * from the BBOB (Black-Box Optimization Benchmarking) test suite.
 *
 * The Skew Rastrigin-Bueche function is defined as:
 *
 * f(x) = 10 * (D - sum_{i=1}^{D} cos(2 * pi * z_i)) + sum_{i=1}^{D} z_i^2 + 100 * f_pen(x) + f_opt
 *
 * where z_i = s_i * T_osz(x_i - x_i^opt) for i = 1, ..., D
 *
 * The function is highly multimodal with a structured but highly asymmetric placement of the optima.
 * It is constructed as a deceptive function for symmetrically distributed search operators.
 *
 * For more details, refer to the BBOB documentation:
 * https://coco.gforge.inria.fr/downloads/download16.00/bbobdocfunctions.pdf
 */


public class SkewRastrigin extends AbstractBBOBProblem {


    public SkewRastrigin(int dimension) {
        super(dimension);
    }

    private static double penalty(double[] x) {
     double sum = 0.0;
     for (double xi : x) {
         double excess = Math.max(0.0, Math.abs(xi) - 5.0);
         sum += excess * excess;
     }
     return sum;
 }



    @Override
    public double evaluate(double[] values) {
        double sum1 = 0;
        double sum2 = 0;
        for (int j = 0; j < values.length; j++) {
            double s = Math.pow(10, 0.5* ((double) j /(dimension-1)) );
            double z = s * BBOBTransformations.tOsz(values[j]);

            if (z > 0 && j % 2 == 0) {
                z *= 10;
            }
            sum1 += Math.cos(2 * Math.PI * z);
            sum2 += z * z;
        }
        double f_pen = penalty(values); // Penalty function, if any
        double f_opt = 0; // Optimal function value, if any
        return 10 * (dimension - sum1) + sum2 + 100 * f_pen + f_opt;
    }



    public static void main(String[] args) {
        SkewRastrigin sr = new SkewRastrigin(3);
        double[] input = {0.0, 0.0, 0.0}; // Global minimum
        double[] input2 = {1.0, 2.0, -1.0};
        double cost = sr.cost(new DoubleVector(input));
        System.out.println("Büche-Rastrigin BBOB cost: " + cost);
    }

}
