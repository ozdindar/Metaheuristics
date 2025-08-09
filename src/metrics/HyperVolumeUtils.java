package metrics;

import java.util.Arrays;
import java.util.List;

public class HyperVolumeUtils {
    public static double[][] boundingHyperRectangle(List<double[]> points, int dimension)
    {
        // Initialize min and max bounds for each dimension
        double[] minBounds = new double[dimension];
        double[] maxBounds = new double[dimension];
        Arrays.fill(minBounds, Double.POSITIVE_INFINITY);
        Arrays.fill(maxBounds, Double.NEGATIVE_INFINITY);

        for (double[] point : points) {
            for (int i = 0; i < dimension; i++) {
                if (point[i] < minBounds[i]) {
                    minBounds[i] = point[i];
                }
                if (point[i] > maxBounds[i]) {
                    maxBounds[i] = point[i];
                }
            }
        }

        // Return the bounding hyper-rectangle as a 2D array
        return new double[][]{minBounds, maxBounds};
    }

    public static double calculateVolume(double[][] bounds) {
        double[] minBounds = bounds[0];
        double[] maxBounds = bounds[1];
        double volume = 1.0;

        for (int i = 0; i < minBounds.length; i++) {
            volume *= (maxBounds[i] - minBounds[i]);
        }

        return volume;
    }
}
