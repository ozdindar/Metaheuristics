package problems.bbob.f15_rastrigin;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.BBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.DoubleVector;
import representation.base.Representation;

import java.util.Random;

public class Rastrigin extends AbstractBBOBProblem {


    private double[] xOpt;

    double [][] R;
    double [][] Q;
    double[] lambda;

    public Rastrigin(int dimension,Random rand) {
        super(dimension);

        R= BBOBTransformations.generateOrthogonalMatrix(dimension,rand);
        Q= BBOBTransformations.generateOrthogonalMatrix(dimension, rand);
        lambda = BBOBTransformations.createLambda(dimension,10);
        xOpt = new double[dimension];
    }



    public double evaluate(double[] x) {
        int n = x.length;
        double[] z = new double[n];

        // Step 1: Shift
        for (int i = 0; i < n; i++) {
            z[i] = x[i] - xOpt[i];
        }

        // Step 2: Rotation R
        z = BBOBTransformations.multiply(R, z);



        // Step 3: Non-linear Transformation T_osz
        z = BBOBTransformations.tOsz(z);

        // Step 4: Asymmetric Transformation T_asy
        z = BBOBTransformations.tAsy(z, 0.2);

        // Step 5: Rotation Q
        z = BBOBTransformations.multiply(Q, z);

        // Step 6: Scaling
        BBOBTransformations.applyLambda(z,lambda);
        for (int i = 0; i < n; i++) {
            z[i] *= lambda[i];
        }

        // Step 7: 2nd Rotation R
        z = BBOBTransformations.multiply(R, z);



        // Rastrigin function evaluation
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += z[i] * z[i] - 10.0 * Math.cos(2 * Math.PI * z[i]);
        }

        return 10 * n + sum;
    }



    public static void main(String[] args) {
        // Example usage

        Rastrigin rastrigin = new Rastrigin(2,new Random(42));

        double[] x = {0.0, 0.0}; // Example input vector
        double result = rastrigin.cost(new DoubleVector(x));
        System.out.println("Rastrigin function value: " + result);
    }

    @Override
    public int getDimension() {
        return dimension;
    }
}
