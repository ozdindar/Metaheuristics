package problems.bbob.f20_schwefel;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.BBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.DoubleVector;
import representation.base.Representation;

import java.util.Arrays;

public class Schwefel extends AbstractBBOBProblem {


    private static final double FOPT = 0.0; // Replace with actual f_opt value if known
    private static final double CONSTANT = 4.189828872724339;
    private static final double X_OPT = 4.2096874633 / 2;

    public Schwefel(int dimension) {
        super(dimension);
    }

    @Override
    public double evaluate(double[] x) {
        int D = x.length;
        double[] z = transform(x);
        double sum = 0.0;

        for (int i = 0; i < D; i++) {
            sum += z[i] * Math.sin(Math.sqrt(Math.abs(z[i])));
        }

        double penalty = BBOBTransformations.penalty(BBOBTransformations.divided(z,100.0));
        return -sum / (100.0 * D) + CONSTANT + 100.0 * penalty + FOPT;
    }

    private static double[] transform(double[] x) {
        int D = x.length;
        double[] xHat = new double[D];
        double[] zHat = new double[D];
        double[] z = new double[D];

        // Step 1: xHat = 2 * alternatingSignVector * x
        for (int i = 0; i < D; i++) {
            int sign = (i % 2 == 0) ? -1 : 1;
            xHat[i] = 2.0 * sign * x[i];
        }

        // Step 2: zHat transformation
        zHat[0] = xHat[0];
        for (int i = 1; i < D; i++) {
            zHat[i] = xHat[i] + 0.25 * (xHat[i - 1] - 2.0 * Math.abs(X_OPT));
        }

        // Step 3: Apply Lambda^10 scaling and shift
        for (int i = 0; i < D; i++) {
            double exponent = (double) i / (D - 1);
            double lambda = Math.pow(10.0, exponent*0.5);
            z[i] = 100.0 * ( lambda * (zHat[i] - 2.0 * Math.abs(X_OPT)) + 2.0*Math.abs(X_OPT));
        }

        return z;
    }


    public static void runTests() {
        Schwefel sch = new Schwefel(2);
        double[][] testInputs = {
                {4.2096874633 / 2, 4.2096874633 / 2},
                {0.0, 0.0},
                {500.0, 500.0},
                {-4.2096874633 / 2, 4.2096874633 / 2},
                {100.0, -300.0}
        };

        for (int i = 0; i < testInputs.length; i++) {
            double result = sch.cost(new DoubleVector(testInputs[i]));
            System.out.printf("Test Case %d: Schwefel(%s) = %.6f%n", i + 1, Arrays.toString(testInputs[i]), result);
        }
    }


    public static void main(String[] args) {
        runTests();

    }
}
