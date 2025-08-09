package problems.bbob.f10_ellipsoid;

import problems.bbob.AbstractBBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.base.Representation;

import java.util.Random;

public class Ellipsoid extends AbstractBBOBProblem {

    double[][] R;

    public Ellipsoid(int dimension, Random rnd) {
        super(dimension);

        R= BBOBTransformations.generateOrthogonalMatrix(dimension,rnd);
    }



    @Override
    public boolean isFeasible(Representation i) {
        // For the Ellipsoid Separable function, all points are feasible
        return true;
    }

    @Override
    public double evaluate(double[] values) {
        double sum = 0;
        double[]Rx= BBOBTransformations.multiply(R,values);

        for (int j = 0; j < values.length; j++) {
            double z= BBOBTransformations.tOsz(Rx[j]);
            sum += Math.pow(10, 6.0 * j / (dimension - 1)) * z*z;
        }
        return sum;
    }

}
