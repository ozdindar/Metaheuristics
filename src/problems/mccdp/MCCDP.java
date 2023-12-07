package problems.mccdp;

import base.OptimizationProblem;
import representation.IntegerPermutation;
import representation.IntegerVector;
import representation.base.Representation;

import java.util.ArrayList;

/**
 * Created by dindar.oz on 1.07.2016.
 */
public class MCCDP implements OptimizationProblem {


    final int SENSOR_COST = 10;
    final int NON_COVERAGE_PENALTY = 40;

    public WSNModel getWsnModel() {
        return wsnModel;
    }

    final WSNModel wsnModel;


    public MCCDP(WSNModel model)
    {
        wsnModel = model;
    }
    public MCCDP(int rowCount, int columnCount, double height, double width, double sensorRange) {

        this.wsnModel = new WSNGridModel(rowCount,columnCount,height,width,sensorRange);
    }




    @Override
    public boolean isFeasible(Representation i) {
        IntegerVector iv = decode(i);

        for (Integer n:iv.getList())
        {
            if (n<0 || n>= wsnModel.sensorPointCount())
                return false;
        }

        return true;
    }

    @Override
    public double cost(Representation i) {
        IntegerVector iv = decode(i);
        //int coverage[][] = wsnModel.calculateCoverageCounts(iv.getList());
        //int redundantCoverage = wsnModel.redundantCoverage(coverage);
        int r = wsnModel.noncoveredPointsCount(iv.getList());
        return r*NON_COVERAGE_PENALTY + iv.getLength()*SENSOR_COST ;
    }

    public IntegerVector decode(Representation i) {
        if (i instanceof IntegerVector)
            return (IntegerVector)i;

        if (i instanceof IntegerPermutation)
        {
            IntegerPermutation ip = (IntegerPermutation)i;
            return wsnModel.firstFit(ip.getList());
        }

        return new IntegerVector(new ArrayList<>());
    }

    @Override
    public double maxDistance() {
        return wsnModel.getMaxDistance();
    }

    public int targetPointCount() {
        return wsnModel.targetPointCount();
    }
}
