package metrics.stn;

import metrics.HyperVolumeUtils;
import representation.DoubleVector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class STNUtils {

    public static double[][] boundingHyperRectangle(Collection<STNNode> nodes, int dimension)
    {
        // Initialize min and max bounds for each dimension
        List<double[]> points = nodes.stream().map(x->((DoubleVector)x.bRep).getValues()).collect(Collectors.toList());
        return HyperVolumeUtils.boundingHyperRectangle(points,dimension);
    }

    public static double[][] boundingHyperRectangle(STN stn, int dimension, long stayThreshold) {
        return boundingHyperRectangle(stn.nodeMap.values().stream().filter(x->x.totalStay>=stayThreshold).collect(Collectors.toList()),dimension);
    }

    public static double[][] boundingHyperRectangle(STN stn, int dimension, Predicate<STNNode> predicate) {
        return boundingHyperRectangle(stn.nodeMap.values().stream().filter(predicate).collect(Collectors.toList()),dimension);
    }




    public static void printStats(STN stn, int dimension)
    {
        int stayThreshold = 3;
        System.out.println(stn);
        double[][] hRec = STNUtils.boundingHyperRectangle(stn,dimension,stayThreshold);
        System.out.println("Hyper Rectangle:" + Arrays.deepToString(hRec));
        System.out.println("HRV:"+ HyperVolumeUtils.calculateVolume(hRec));
        System.out.println("# of Locations: " + stn.nodeMap.values().stream().filter(x->x.totalStay>=stayThreshold).count() );

    }

    public static void printStats(STN stn, int dimension, Predicate<STNNode> filter)
    {
        int stayThreshold = 3;
        stn.getNodes(filter).forEach(System.out::println);
        double[][] hRec = STNUtils.boundingHyperRectangle(stn, dimension, filter);
        System.out.println("Hyper Rectangle:" + Arrays.deepToString(hRec));
        System.out.println("HRV:"+ HyperVolumeUtils.calculateVolume(hRec));
        System.out.println("# of Locations: " + stn.getNodes(filter).size() );

    }
}
