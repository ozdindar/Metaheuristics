package problems.mccdp;

import util.random.RandUtil;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 6.06.2017.
 */
public class WSNModelFactory {

    public  static WSNModel generateGridModel(int rowCount, int columnCount, double height, double width, double sensorRange)
    {
        return new WSNGridModel(rowCount,columnCount,height,width,sensorRange);
    }


    public  static WSNModel generateRandomListModel(double height, double width, int targetCount, int sensorCount, double sensorRange)
    {
        List<Point2D> targetPoints = new ArrayList<>(targetCount);
        for (int t = 0; t < targetCount; t++) {
            Point2D target = new Point2D.Double(RandUtil.randDouble(0,width),RandUtil.randDouble(0,height));
            targetPoints.add(target);
        }

        List<Point2D> sensorPoints = new ArrayList<>(sensorCount);
        for (int s = 0; s < sensorCount; s++) {
            Point2D sensor = new Point2D.Double(RandUtil.randDouble(0,width),RandUtil.randDouble(0,height));
            sensorPoints.add(sensor);
        }

        WSNModel model = new WSNListModel(height,width,sensorRange,targetPoints,sensorPoints);
        return model;
    }
}
