package metrics;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public class HistogramPlotter {



    public static <K,V> void createHistogram(Map<K, V> map, Function<V,Number> valueExtractor, String title, String rTitle, String cTitle) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            dataset.addValue(valueExtractor.apply(entry.getValue()), "Visit Count", (Comparable) entry.getKey());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                rTitle,
                cTitle,
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(barChart));
        frame.pack();
        frame.setVisible(true);
    }

    public static <K, V> void createMultiHistogram(
            Map<K, V> map,
            Map<String, Function<V, Number>> valueExtractors,
            String title,
            String rTitle,
            String cTitle,
            String outputFilePath) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();

            for (Map.Entry<String, Function<V, Number>> extractorEntry : valueExtractors.entrySet()) {
                String seriesName = extractorEntry.getKey();
                Number extractedValue = extractorEntry.getValue().apply(value);
                dataset.addValue(extractedValue, seriesName, (Comparable<?>) key);
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                title,
                rTitle,
                cTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false); // Enable legend


        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(1200, 800));



        JFrame frame = new JFrame("Histogram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);


        if (outputFilePath != null && !outputFilePath.isEmpty()) {
            try {
                ChartUtilities.saveChartAsPNG(new File(outputFilePath), barChart, 1200, 800);
                System.out.println("Chart saved to: " + outputFilePath);
            } catch (IOException e) {
                System.err.println("Failed to save chart: " + e.getMessage());
            }
        }

    }



}
