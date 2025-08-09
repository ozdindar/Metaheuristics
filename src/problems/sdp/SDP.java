package problems.sdp;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import math.geom2d.Point2D;
import representation.IntegerVector;
import representation.base.Representation;

import java.util.List;

/**
 * Created by dindar.oz on 17.05.2017.
 */
public class SDP implements OptimizationProblem {

    private static final double UNCOVERED_TARGET_PENALTY = 1.0;
    DeploymentSpace deploymentSpace;
    List<Sensor> sensorList;
    List<Point2D> targetList;

    @Override
    public boolean isFeasible(Representation rep) {
        if (!(rep instanceof IntegerVector))
            return false;
        IntegerVector iv = (IntegerVector)rep;

        for (int i = 0; i < iv.getLength(); i++) {
            if (iv.get(i)>=deploymentSpace.size())
                return false;
            for (int j = i+1; j <iv.getLength() ; j++) {
                if (iv.get(i).equals(iv.get(j)))
                    return false;
            }
        }
        return true;
    }

    @Override
    public double cost(Representation rep) {
        if (!(rep instanceof IntegerVector))
            throw new InvalidIndividual("SDP expects IntegerVector");
        IntegerVector iv = (IntegerVector)rep;

        double totalPrice =0;
        for (int i:iv.getList()) {
            Deployment deployment = deploymentSpace.get(i);

            totalPrice += deployment.sensor.price;
        }

        int uncoveredTarget=0;
        for (Point2D target:targetList)
        {
            boolean covered = false;
            for (int i:iv.getList()) {
                Deployment deployment = deploymentSpace.get(i);

                if (deployment.covers(target)) {
                    covered = true;
                    break;
                }
            }
            if (!covered)
                uncoveredTarget++;
        }

        return totalPrice+ UNCOVERED_TARGET_PENALTY*(targetList.size()-uncoveredTarget);
    }

    @Override
    public double maxDistance() {
        throw  new RuntimeException("Not implemented");
        //return 0;
    }
}
