package problems.mccdp;

import representation.IntegerVector;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 1.07.2016.
 */
public class WSNListModel implements WSNModel{

    final double height;
    final double width;
    final double sensorRange;

    List<Point2D> targetPoints;
    List<Point2D> sensorPoints;


    public WSNListModel( double height, double width, double sensorRange,List<Point2D> targetPoints,List<Point2D> sensorPoints) {

        this.height = height;
        this.width = width;
        this.sensorRange = sensorRange;
        this.targetPoints = targetPoints;
        this.sensorPoints = sensorPoints;



    }




    public List<Integer> neighborsOf(int sensorIndex, double range)
    {
        List<Integer> neighbors = new ArrayList<>();

        Point2D sensorPos = sensorPoints.get(sensorIndex);
        for (int s = 0; s < sensorPoints.size() ; s++) {
           if (sensorPoints.get(s).distance(sensorPos)<=range)
               neighbors.add(s);
        }
        return neighbors;
    }







    public double getMaxDistance() {
        return sensorPointCount();
    }

    @Override
    public int targetPointCount() {
        return targetPoints.size();
    }

    @Override
    public Point2D[] getTargetPoints() {
       return (Point2D[]) targetPoints.toArray();
    }

    @Override
    public boolean isCovered(int targetIndex, List<Integer> sensorPlacement) {
        Point2D targetPos = targetPoints.get(targetIndex);

        for(Integer i : sensorPlacement)
        {
            if (targetPos.distance(sensorPoints.get(i))<=sensorRange)
                return true;
        }
        return false;
    }

    @Override
    public Point2D getTargetPosition(int targetIndex) {

        return targetPoints.get(targetIndex);
    }

    @Override
    public Point2D getSensorPosition(Integer sensorIndex) {
        return sensorPoints.get(sensorIndex);
    }

    @Override
    public double getSensorRange() {
        return sensorRange;
    }

    @Override
    public Rectangle2D getBoundingRect() {
        return new Rectangle2D.Double(0,0,width,height);
    }




    public IntegerVector firstFit(List<Integer> ip) {
        List<Integer> nodeList= new ArrayList<>();
        int coverageCounts[] = new int[targetPoints.size()];


        for (Integer i: ip)
        {
            nodeList.add(i);
            coverageCounts = deltaCoverage(coverageCounts,i);

            if (coveredPointsCount(coverageCounts)==targetPointCount())
                return new IntegerVector(nodeList);
        }
        return new IntegerVector(nodeList);
    }

    public int coveredPointsCount(int[] coverageCounts) {
        int count=0;
        for (int i = 0; i < coverageCounts.length; i++) {
            if (coverageCounts[i]>0)
                count++;
        }
        return count;
    }


    @Override
    public int[] deltaCoverage(int[] coverageCounts, int oldSensorIndex, int newSensorIndex) {
        Point2D oldSensorPos = sensorPoints.get(oldSensorIndex);
        int newCoverage[] = Arrays.copyOf(coverageCounts,coverageCounts.length);
        for (int t = 0; t < targetPoints.size(); t++) {
            if (targetPoints.get(t).distance(oldSensorPos)<=sensorRange)
                newCoverage[t]--;
        }
        Point2D newSensorPos = sensorPoints.get(newSensorIndex);
        for (int t = 0; t < targetPoints.size(); t++) {
            if (targetPoints.get(t).distance(newSensorPos)<=sensorRange)
                newCoverage[t]++;
        }
        return newCoverage;
    }


    @Override
    public int[] deltaCoverage(int[] coverageCounts, int sensorIndex) {
        Point2D sensorPos = sensorPoints.get(sensorIndex);
        int newCoverage[] = Arrays.copyOf(coverageCounts,coverageCounts.length);
        for (int t = 0; t < targetPoints.size(); t++) {
            if (targetPoints.get(t).distance(sensorPos)<=sensorRange)
                newCoverage[t]++;
        }
        return newCoverage;
    }



    public int[] calculateCoverageCounts(List<Integer> sensorPositions) {
        int coverageCounts[] = new int[targetPointCount()];
        for ( Integer s : sensorPositions)
        {
            for (int t = 0; t < targetPointCount(); t++) {
                if (targetPoints.get(t).distance(sensorPoints.get(s))<=sensorRange)
                    coverageCounts[t]++;
            }

        }
        return coverageCounts;
    }

    public int redundantCoverage(int coverageCounts[] ) {
        int sum=0;
        for (int t = 0; t< coverageCounts.length; t++)
        {
                if (coverageCounts[t]>1)
                    sum+=coverageCounts[t]-1;

        }
        return sum;
    }

    @Override
    public int sensorPointCount() {

        return sensorPoints.size();
    }

    public int noncoveredPointsCount(List<Integer> list) {
        return sensorPointCount()-coveredPointsCount(calculateCoverageCounts(list));
    }
}
