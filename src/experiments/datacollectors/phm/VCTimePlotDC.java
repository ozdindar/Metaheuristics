package experiments.datacollectors.phm;

import experiments.ExperimentCase;
import metrics.DataCollector;
import metrics.populationheatmap.PHM;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class VCTimePlotDC implements DataCollector<PHM> {
    PHM phm;
    private ExperimentCase experimentCase;
    private int repeat;
    private final String fileName;

    double[] visitCounts;
    double slicing;

    public VCTimePlotDC(String fileName, double slicing) {
        this.fileName = fileName;
        this.slicing= slicing;
    }

    @Override
    public void collect(PHM phm) {

        repeat++;
        this.phm= phm;

        // TODO: Time based plot should be generated here.
        long maxIteration= phm.iteration();

        for (int i=0,iteration = (int) (slicing*maxIteration); iteration <= maxIteration; iteration += (int) (slicing*maxIteration), i++) {
            int finalIteration = iteration;
            visitCounts[i] += phm.getNodes(node -> node.firstVisit > 0 && node.firstVisit <= finalIteration).size();
        }
    }

    @Override
    public String resultAsString() {

        String path = fileName+experimentCase.getTitle() +".png";

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        long maxIteration= phm.iteration();

        for (int i=0, iteration = (int) (slicing*maxIteration); iteration <= maxIteration; iteration += (int) (slicing*maxIteration), i++) {
            long count = (long) (visitCounts[i]/repeat);
            dataset.addValue(count, "Visited Nodes", String.valueOf(iteration));
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Visited Nodes Over Iterations",
                "Iteration",
                "Number of Visited Nodes",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        try {
            ChartUtilities.saveChartAsPNG(new File(path), lineChart, 1200, 800);
            System.out.println("Chart saved to: " + path);
        } catch (IOException e) {
            System.err.println("Failed to save chart: " + e.getMessage());
        }


        return "";
    }

    @Override
    public void init(ExperimentCase experimentCase) {
        repeat =0;
        visitCounts = new double[(int) (1/slicing)];
        this.experimentCase = experimentCase;

    }
}
