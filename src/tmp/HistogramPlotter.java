package tmp;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import representation.base.Representation;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class HistogramPlotter {

    public static class PHMNode {
        private final String id;
        public long firstVisit;
        public long lastVisit;
        public long firstTouch;
        public long lastTouch;
        Representation bRep;
        double bCost;
        long bCostUpdate;
        private long touchCount;
        private long visitCount;

        public PHMNode(String id, long visitCount) {
            this.id = id;
            this.visitCount = visitCount;
        }

        public String getId() {
            return id;
        }

        public long getVisitCount() {
            return visitCount;
        }
    }

    public static void createHistogram(Map<String, PHMNode> nodeMap) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, PHMNode> entry : nodeMap.entrySet()) {
            dataset.addValue(entry.getValue().getVisitCount(), "Visit Count", entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Visit Count per Node",
                "Node ID",
                "Visit Count",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(barChart));
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Map<String, PHMNode> nodeMap = new HashMap<>();
        nodeMap.put("node1", new PHMNode("node1", 15));
        nodeMap.put("node2", new PHMNode("node2", 30));
        nodeMap.put("node3", new PHMNode("node3", 22));
        nodeMap.put("node4", new PHMNode("node4", 10));
        nodeMap.put("node5", new PHMNode("node5", 18));

        createHistogram(nodeMap);
    }
}
