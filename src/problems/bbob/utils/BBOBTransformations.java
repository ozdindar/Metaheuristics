package problems.bbob.utils;

import java.util.Arrays;
import java.util.Random;

public class BBOBTransformations {

    // Applies the T_osz transformation

    /**
     * T_osz transformation:
     * Applies a non-linear distortion to each element of the input vector.
     *
     * Mathematical formulation:
     * Let x ≠ 0, then:
     *   z = sign(x) * exp(log(|x|) + 0.049 * (sin(c1 * log(|x|)) + sin(c2 * log(|x|))))
     * where:
     * c1 = 10 if x > 0, else 5.5
     * c2 = 7.9 if x > 0, else 3.1
     *
     * This transformation introduces non-smoothness and irregularity.
     */
    public static double tOsz(double x) {
        if (x == 0.0) return 0.0;

        double c1 = x > 0 ? 10 : 5.5;
        double c2 = x > 0 ? 7.9 : 3.1;

        double logX = Math.log(Math.abs(x));
        double sign = Math.signum(x);
        double sin1 = Math.sin(c1 * logX);
        double sin2 = Math.sin(c2 * logX);

        return sign * Math.exp(logX + 0.049 * (sin1 + sin2));
    }

    public static double[] tOsz(double[] z) {
        int n = z.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            if (z[i] != 0) {
                double c1 = Math.log(Math.abs(z[i]));
                double c2 = Math.sin(10 * c1);
                result[i] = Math.signum(z[i]) * Math.exp(c1 + 0.049 * (Math.sin(c1) + c2));
            } else {
                result[i] = 0;
            }
        }
        return result;
    }


    /**
     * T_asy transformation:
     * Introduces asymmetry into the input vector.
     *
     * Mathematical formulation:
     * For each element x_i:
     * if x_i > 0:
     * z_i = x_i ^ (1 + β * i / (n - 1) * sqrt(x_i))
     * else:
     * z_i = x_i
     *
     * This breaks symmetry and adds complexity to the landscape.
     */

    public static double[] tAsy(double[] x, double beta) {
        int n = x.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            if (x[i] > 0) {
                result[i] = Math.pow(x[i], 1 + beta * i / (n - 1) * Math.sqrt(x[i]));
            } else {
                result[i] = x[i];
            }
        }
        return result;
    }


    /**
     * Diagonal scaling (Lambda):
     * Applies conditioning to the problem by scaling each variable.
     *
     * Mathematical formulation:
     * For each element x_i:
     * z_i = x_i * α^{0.5 * i / (n - 1)}
     *
     * This introduces ill-conditioning to the problem.
     */

    public static double[] applyLambda(double[] x, double alpha) {
        int n = x.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double exponent = (double) i / (n - 1);
            double lambda = Math.pow(alpha, 0.5 * exponent);
            result[i] = lambda * x[i];
        }
        return result;
    }

    public static void applyLambda(double[] vector, double[] lambda) {
        int n = vector.length;
        for (int i = 0; i < n; i++) {
            vector[i] *= lambda[i] ;
        }
    }

    public static double[] createLambda(int dimension, double alpha) {
        double[] lambda = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            lambda[i] = Math.pow(alpha, 0.5 * i / (dimension - 1));
        }
        return lambda;
    }

    // Full transformation pipeline
    private static double[] transform(double[] x, double beta, double alpha) {
        double[] z = Arrays.stream(x).map(BBOBTransformations::tOsz).toArray();
        z = tAsy(z, beta);
        z = applyLambda(z, alpha);
        return z;
    }

    // Generate a random orthogonal matrix using Gram-Schmidt
    public static double[][] generateOrthogonalMatrix(int dimension, Random rand) {

        double[][] matrix = new double[dimension][dimension];

        // Fill with standard normal values
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                matrix[i][j] = rand.nextGaussian();
            }
        }

        // Apply Gram-Schmidt process
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < i; j++) {
                double dot = 0.0;
                for (int k = 0; k < dimension; k++) {
                    dot += matrix[i][k] * matrix[j][k];
                }
                for (int k = 0; k < dimension; k++) {
                    matrix[i][k] -= dot * matrix[j][k];
                }
            }
            // Normalize
            double norm = 0.0;
            for (int k = 0; k < dimension; k++) {
                norm += matrix[i][k] * matrix[i][k];
            }
            norm = Math.sqrt(norm);
            for (int k = 0; k < dimension; k++) {
                matrix[i][k] /= norm;
            }
        }

        return matrix;
    }

    public static double[] multiply(double[][] matrix, double[] vector) {
        int n = vector.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = 0.0;
            for (int j = 0; j < n; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }

    public static double penalty(double[] x) {
        double sum = 0.0;
        for (double xi : x) {
            double excess = Math.max(0.0, Math.abs(xi) - 5.0);
            sum += excess * excess;
        }
        return sum;
    }

    public static double[] divided(double[] z, double divider) {
        double[] nz = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            nz[i]= z[i]/divider;
        }
        return nz;
    }
}
