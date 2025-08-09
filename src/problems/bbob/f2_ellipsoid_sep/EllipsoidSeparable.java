package problems.bbob.f2_ellipsoid_sep;

import base.OptimizationProblem;
import problems.bbob.AbstractBBOBProblem;
import problems.bbob.BBOBProblem;
import problems.bbob.utils.BBOBTransformations;
import representation.DoubleVector;
import representation.base.Representation;

public class EllipsoidSeparable extends AbstractBBOBProblem {

    public EllipsoidSeparable(int dimension) {
        super(dimension);
    }



    @Override
    public boolean isFeasible(Representation i) {
        // For the Ellipsoid Separable function, all points are feasible
        return true;
    }

    @Override
    public double evaluate(double[] values) {
        double sum = 0;
        for (int j = 0; j < values.length; j++) {
            double z= BBOBTransformations.tOsz(values[j]);
            sum += Math.pow(10, 6.0 * j / (dimension - 1)) * z*z;
        }
        return sum;
    }

}
