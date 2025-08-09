package metrics.stn;

import metrics.HyperVolumeUtils;
import org.junit.jupiter.api.Test;
import representation.DoubleVector;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class BoundingHyperRectangleTest {

    @Test
    public void testEmptySTN() {
        STN stn = new STN();
        double[][] bounds = STNUtils.boundingHyperRectangle(stn,2 ,1);
        assertEquals(Double.POSITIVE_INFINITY, bounds[0][0]);
        assertEquals(Double.NEGATIVE_INFINITY, bounds[1][0]);
   }

    @Test
    public void testSingleNode() {
        STN stn = new STN();
        STNNode node = new STNNode("node1");
        node.bRep = new DoubleVector(new double[]{1.0, 2.0});
        node.totalStay = 5;
        stn.addNode(node);

        double[][] bounds = STNUtils.boundingHyperRectangle(stn, 2,1);
        assertArrayEquals(new double[]{1.0, 2.0}, bounds[0]);
        assertArrayEquals(new double[]{1.0, 2.0}, bounds[1]);

        System.out.println(Arrays.deepToString(bounds));
    }

    @Test
    public void testMultipleNodes() {
        STN stn = new STN();
        STNNode node1 = new STNNode("node1");
        node1.bRep = new DoubleVector(new double[]{1.0, 2.0});
        node1.totalStay = 5;
        stn.addNode(node1);

        STNNode node2 = new STNNode("node2");
        node2.bRep = new DoubleVector(new double[]{3.0, 4.0});
        node2.totalStay = 10;
        stn.addNode(node2);

        double[][] bounds = STNUtils.boundingHyperRectangle(stn,2, 1);
        assertArrayEquals(new double[]{1.0, 2.0}, bounds[0]);
        assertArrayEquals(new double[]{3.0, 4.0}, bounds[1]);

        System.out.println(Arrays.deepToString(bounds));
        System.out.println("Volume:" + HyperVolumeUtils.calculateVolume(bounds));
    }

    @Test
    public void testThresholdFiltering() {
        STN stn = new STN();
        STNNode node1 = new STNNode("node1");
        node1.bRep = new DoubleVector(new double[]{1.0, 2.0});
        node1.totalStay = 5;
        stn.addNode(node1);

        STNNode node2 = new STNNode("node2");
        node2.bRep = new DoubleVector(new double[]{3.0, 4.0});
        node2.totalStay = 1;
        stn.addNode(node2);

        double[][] bounds = STNUtils.boundingHyperRectangle(stn,2, 5);
        assertArrayEquals(new double[]{1.0, 2.0}, bounds[0]);
        assertArrayEquals(new double[]{1.0, 2.0}, bounds[1]);

        System.out.println(Arrays.deepToString(bounds));
    }

    @Test
    public void testThresholdFilteringFirstVisit() {
        STN stn = new STN();
        stn.setIteration(100);
        STNNode node1 = new STNNode("node1");
        node1.bRep = new DoubleVector(new double[]{1.0, 2.0});
        node1.totalStay = 5;
        node1.firstVisit = 90;
        stn.addNode(node1);

        STNNode node2 = new STNNode("node2");
        node2.bRep = new DoubleVector(new double[]{3.0, 4.0});
        node2.totalStay = 1;
        node2.firstVisit = 50;
        stn.addNode(node2);

        double[][] bounds = STNUtils.boundingHyperRectangle(stn, 2, (x)->x.firstVisit>45);
        assertArrayEquals(new double[]{1.0, 2.0}, bounds[0]);
        assertArrayEquals(new double[]{3.0, 4.0}, bounds[1]);

        System.out.println(Arrays.deepToString(bounds));
    }
}
