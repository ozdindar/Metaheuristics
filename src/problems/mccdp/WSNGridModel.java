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
public class WSNGridModel implements WSNModel{
    final int rowCount;
    final int columnCount;
    final double height;
    final double width;
    final double sensorRange;

    final int sensorRangeIndexV;
    final int sensorRangeIndexH;


    final double sensorNeighborDistanceV;
    final double sensorNeighborDistanceH;

    boolean coverageMatrix[][][][];


    public WSNGridModel(int rowCount, int columnCount, double height, double width, double sensorRange) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.height = height;
        this.width = width;
        this.sensorRange = sensorRange;
        sensorNeighborDistanceV = this.height /(double) this.rowCount;
        sensorRangeIndexV = (int) (sensorRange/ sensorNeighborDistanceV);

        sensorNeighborDistanceH = this.width /(double) this.columnCount;
        sensorRangeIndexH = (int) (sensorRange/ sensorNeighborDistanceH);

        buildCoverageMatrix();
    }

    private void buildCoverageMatrix() {
        coverageMatrix = new boolean[rowCount][columnCount][rowCount][columnCount];

        for (int r1 = 0; r1 < rowCount; r1++) {
            for (int c1 = 0; c1 < columnCount; c1++) {
                for (int r2 = 0; r2 < rowCount; r2++) {
                    for (int c2 = 0; c2 < columnCount; c2++) {
                        double dr = Math.abs(r1-r2)* sensorNeighborDistanceV;
                        double dc = Math.abs(c1-c2)* sensorNeighborDistanceH;
                        coverageMatrix[r1][c1][r2][c2] = (Math.sqrt(dr*dr + dc*dc)<=sensorRange) ;
                    }
                }
            }
        }
    }


    public List<Integer> coveredPoints(int row ,int col)
    {
        List<Integer> coveredPoints = new ArrayList<>();
        for (int r = Math.max(row- sensorRangeIndexV,0); r<=row+ sensorRangeIndexV && r<rowCount; r++)
        {
            for (int c = Math.max(col- sensorRangeIndexH,0); c<=col+ sensorRangeIndexH && c<columnCount; c++)
            {
                if (r<0|| c<0 || r>= rowCount || c>= columnCount)
                    continue;

                if (r == row || c ==col )
                {
                    coveredPoints.add(toIntegerIndex(r,c));
                }
                else if (coverageMatrix[r][c][row][col])
                    coveredPoints.add(toIntegerIndex(r,c));
            }
        }
        return coveredPoints;
    }

    private Integer toIntegerIndex(int row, int col) {
        return row*columnCount+col;
    }

    @Override
    public List<Integer> neighborsOf(int point, double range)
    {
        List<Integer> neighbors = new ArrayList<>();
        int row = point/columnCount;
        int col = point%columnCount;

        int dRange = (int) (range/sensorNeighborDistanceH);
        for (int d=1;d<=dRange;d++)
        {
            int arr[][]= new int[8][2];
            arr[0][0] = row;    arr[0][1] = col-1;
            arr[1][0] = row-1;  arr[1][1] = col;
            arr[2][0] = row-1;  arr[2][1] = col-1;
            arr[3][0] = row-1;  arr[3][1] = col+1;
            arr[4][0] = row;    arr[4][1] = col+1;
            arr[5][0] = row+1;  arr[5][1] = col+1;
            arr[6][0] = row+1;  arr[6][1] = col-1;
            arr[7][0] = row+1;  arr[7][1] = col;
            for (int i = 0; i <arr.length; i++) {
                if (arr[i][0]>=0 && arr[i][0]<rowCount && arr[i][1]>=0 && arr[i][1]<columnCount)
                    neighbors.add(toIntegerIndex(arr[i][0],arr[i][1]));

            }
        }
        return neighbors;
    }

    public int coveredPointsCount( int coverageCounts[])
    {
        int sum=0;
        for (int t=0; t<coverageCounts.length;t++)
        {
            if (coverageCounts[t]>0)
                sum++;
        }
        return sum;
    }

    public int[] calculateCoverageCounts(List<Integer> sensorPositions) {
        int coverageCounts[] = new int[rowCount*columnCount];
        for ( Integer i : sensorPositions)
        {
            int row = i/ columnCount;
            int col = i% columnCount;

            for (int r = Math.max(row- sensorRangeIndexV,0); r<=row+ sensorRangeIndexV && r<rowCount; r++)
            {
                for (int c = Math.max(col- sensorRangeIndexH,0); c<=col+ sensorRangeIndexH && c<columnCount; c++)
                {
                    if (r<0|| c<0 || r>= rowCount || c>= columnCount)
                        continue;

                    if (r == row || c ==col)
                    {
                        coverageCounts[toIntegerIndex(r,c)]++;
                        continue;
                    }

                    else if (coverageMatrix[row][col][r][c])
                        coverageCounts[toIntegerIndex(r,c)]++;
                }
            }
        }
        return coverageCounts;
    }


    public int[]deltaCoverage(int coverage[], int oldSensor, int newSensor) {
        int coverageCounts[] = Arrays.copyOf(coverage,coverage.length);

        int oldSensorRow = oldSensor/columnCount;
        int oldSensorCol = oldSensor%columnCount;

        int newSensorRow = newSensor/columnCount;
        int newSensorCol = newSensor%columnCount;
        List<Integer> oldCoverage = coveredPoints(oldSensorRow,oldSensorCol);
        List<Integer> newCoverage = coveredPoints(newSensorRow,newSensorCol);
        for (Integer i:oldCoverage)
        {
            int row = i/columnCount;
            int col = i%columnCount;
            coverageCounts[toIntegerIndex(row,col)]--;
        }
        for (Integer i:newCoverage)
        {
            int row = i/columnCount;
            int col = i%columnCount;
            coverageCounts[toIntegerIndex(row,col)]++;
        }
        return coverageCounts;
    }

    public int[] deltaCoverage(int coverage[], int newSensor) {
        int coverageCounts[] = Arrays.copyOf(coverage,coverage.length);

        int newSensorRow = newSensor/columnCount;
        int newSensorCol = newSensor%columnCount;

        List<Integer> newCoverage = coveredPoints(newSensorRow,newSensorCol);

        for (Integer i:newCoverage)
        {
            int row = i/columnCount;
            int col = i%columnCount;
            coverageCounts[toIntegerIndex(row,col)]++;
        }
        return coverageCounts;
    }



    public double getMaxDistance() {
        return rowCount * columnCount;
    }

    @Override
    public int targetPointCount() {
        return rowCount*columnCount;
    }

    @Override
    public Point2D[] getTargetPoints() {
        Point2D targetPoints[] = new Point2D[rowCount*columnCount];

        for (int r =0; r<rowCount; r++)
        {
            for (int c = 0; c < columnCount; c++) {
                targetPoints[r*columnCount+c]= getPosition(r,c);

            }
        }
        return targetPoints;
    }

    @Override
    public boolean isCovered(int targetIndex, List<Integer> sensorPlacement) {
        int coverage[] = calculateCoverageCounts(sensorPlacement);
        int row = targetIndex/columnCount;
        int column = targetIndex%columnCount;
        return coverage[toIntegerIndex(row,column)]>0;
    }

    @Override
    public Point2D getTargetPosition(int targetIndex) {
        int row = targetIndex/columnCount;
        int column = targetIndex%columnCount;

        return getPosition(row,column);
    }

    @Override
    public Point2D getSensorPosition(Integer sensorIndex) {
        int row = sensorIndex/columnCount;
        int column = sensorIndex%columnCount;

        return getPosition(row,column);
    }

    @Override
    public double getSensorRange() {
        return sensorRange;
    }

    @Override
    public Rectangle2D getBoundingRect() {
        return new Rectangle2D.Double(0,0,width,height);
    }

    Point2D.Double getPosition(int r,int c)
    {
        return new Point2D.Double(c*sensorNeighborDistanceH,r*sensorNeighborDistanceV );
    }

    public Point2D getPosition(int i) {
        int row = i/ columnCount;
        int col = i% columnCount;
        return getPosition(row,col);

    }

    public IntegerVector firstFit(List<Integer> ip) {
        List<Integer> nodeList= new ArrayList<>();
        int coverageCounts[] = new int[rowCount*columnCount];

        for (Integer i: ip)
        {
            nodeList.add(i);
            coverageCounts = deltaCoverage(coverageCounts,i);

            if (coveredPointsCount(coverageCounts)==rowCount*columnCount)
                return new IntegerVector(nodeList);
        }
        return new IntegerVector(nodeList);
    }

    public boolean isCovered(int r, int c, IntegerVector placement) {
        int coverageCounts[] = calculateCoverageCounts(placement.getList());

        return coverageCounts[toIntegerIndex(r,c)]>0;
    }

    public int redundantCoverage(int coverageCounts[][] ) {
        int sum=0;
        for (int r = 0; r< rowCount; r++)
        {
            for (int c = 0; c < columnCount; c++ )
            {
                if (coverageCounts[r][c]>1)
                    sum+=coverageCounts[r][c]-1;
            }
        }
        return sum;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int sensorPointCount() {
        return rowCount*columnCount;
    }

    public int noncoveredPointsCount(List<Integer> list) {
        return sensorPointCount()-coveredPointsCount(calculateCoverageCounts(list));
    }
}
