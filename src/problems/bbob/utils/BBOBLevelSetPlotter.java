package problems.bbob.utils;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;

import problems.bbob.BBOBProblem;
import problems.bbob.f8_rosenbrock.Rosenbrock;

import javax.swing.*;
import java.awt.*;

public class BBOBLevelSetPlotter {

    public static void main(String[] args) {
        plot(new Rosenbrock(2),400);
    }
    public static void plot(BBOBProblem problem, int resolution) {
        if (problem.getDimension() != 2) {
            throw new IllegalArgumentException("Only 2D problems can be plotted.");
        }

        double xMin = problem.lowerBound(0);
        double xMax = problem.upperBound(0);
        double yMin = problem.lowerBound(1);
        double yMax = problem.upperBound(1);

        double stepX = (xMax - xMin) / resolution;
        double stepY = (yMax - yMin) / resolution;

        int totalPoints = resolution * resolution;
        double[] xData = new double[totalPoints];
        double[] yData = new double[totalPoints];
        double[] zData = new double[totalPoints];

        int index = 0;
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                double x = xMin + i * stepX;
                double y = yMin + j * stepY;
                double[] point = new double[]{x, y};
                xData[index] = x;
                yData[index] = y;
                zData[index] = problem.evaluate(point);
                index++;
            }
        }

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double[][] data = new double[][]{xData, yData, zData};
        dataset.addSeries("Level Set", data);

        JFreeChart chart = createChart(dataset, xMin, xMax, yMin, yMax);
        displayChart(chart);
    }

    private static JFreeChart createChart(XYZDataset dataset, double xMin, double xMax, double yMin, double yMax) {
        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setRange(xMin, xMax);
        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setRange(yMin, yMax);

        XYBlockRenderer renderer = new XYBlockRenderer();
        renderer.setBlockWidth((xMax - xMin) / 100);
        renderer.setBlockHeight((yMax - yMin) / 100);

        double zMin = Double.POSITIVE_INFINITY;
        double zMax = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < dataset.getItemCount(0); i++) {
            double z = dataset.getZ(0, i).doubleValue();
            zMin = Math.min(zMin, z);
            zMax = Math.max(zMax, z);
        }

        PaintScale scale = new GrayPaintScale(zMin,zMax);//new GradientPaintScale(zMin, zMax, Color.BLUE, Color.RED);
        renderer.setPaintScale(scale);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.white);

        return new JFreeChart("BBOB Function Level Set", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
    }

    private static void displayChart(JFreeChart chart) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("BBOB Level Set Plot");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new ChartPanel(chart), BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
