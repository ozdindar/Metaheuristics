package problems.mccdp;

import representation.IntegerVector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by dindar.oz on 6.06.2017.
 */
public interface WSNModel {
    int sensorPointCount();

    int noncoveredPointsCount(List<Integer> list);

    IntegerVector firstFit(List<Integer> list);

    double getMaxDistance();

    int targetPointCount();

    Point2D[] getTargetPoints();

    boolean isCovered(int targetIndex, List<Integer> sensorPlacement);

    Point2D getTargetPosition(int targetIndex);

    Point2D getSensorPosition(Integer sensorIndex);

    double getSensorRange();

    Rectangle2D getBoundingRect();

    int[] calculateCoverageCounts(List<Integer> nodes);

    int coveredPointsCount(int[] coverage);

    int[] deltaCoverage(int[] coverage, int oldSensorIndex, int newSensorIndex);

    int[] deltaCoverage(int[] coverage, int newSensorIndex);

    List<Integer> neighborsOf(int node, double range);
}
