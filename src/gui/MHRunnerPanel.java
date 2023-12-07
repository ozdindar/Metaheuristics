package gui;

import base.MutationWrapperNF;
import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import experiments.motap.MOTAPGenerator;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.FileMetaheuristicListener;
import metaheuristic.MetaHeuristicListener;
import metaheuristic.dde.DDE;
import metaheuristic.dpso.DPSO;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.crossover.SimplePermutationCrossover;
import metaheuristic.ea.mutation.*;
import metaheuristic.ea.parentselector.CostBasedParentSelector;
import metaheuristic.ea.terminalcondition.CostBasedTC;
import metaheuristic.ea.terminalcondition.NeighboringBasedTC;
import metaheuristic.ea.terminalcondition.OrCompoundTC;
import metaheuristic.mbo.MBO;
import metaheuristic.pso.DoubleVectorParticleHandler;
import metaheuristic.pso.PSO;
import metaheuristic.sos.SOS;
import metaheuristic.tabu.SolutionMemoryTabuList;
import metaheuristic.tabu.TabuSearch;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import problems.base.InitialSolutionGenerator;
import problems.motap.MOTAProblem;
import problems.motap.mutation.GreedySwapWithEscape;
import problems.motap.mutation.SimpleMOTAPMutation;
import problems.motap.solutionGenerator.MOTAPRandomSG;
import problems.movingpeaks.MP_Scenario;
import problems.movingpeaks.MovingPeaks;
import problems.movingpeaks.crossover.SimpleMPCrossover;
import problems.movingpeaks.mutation.SimpleMPMutation;
import problems.nqueen.NQProblem;
import problems.pcb.PCBData;
import problems.pcb.PCBProblem;
import problems.pcb.mutation.GuidedTwoOpt;
import problems.pcb.terminalcondition.PCBProblemTC;
import util.random.RandUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dindar.oz on 25.06.2015.
 */
public class MHRunnerPanel extends ApplicationFrame {

    private static OptimizationProblem problem = createMPProblem();//createPCBProblem();

    List<MHThread> runnerThreads;

    XYSeriesCollection dataset = new XYSeriesCollection();
    private long updatePeriod= 1;

    public MHRunnerPanel(String title, List<AbstractMetaheuristic> algs, List<InitialSolutionGenerator> solutionGenerators, OptimizationProblem problem ) {
        super(title);
        ChartPanel chartPanel = (ChartPanel)createMHChartPanel();
        chartPanel.setPreferredSize(new Dimension(500, 270));
        this.setContentPane(chartPanel);
        runnerThreads =  new ArrayList<>();//new MHThread(alg,problem,this);

        buildAndRunThreads(algs, solutionGenerators,problem);

    }

    public MHRunnerPanel(String title, AbstractMetaheuristic alg, InitialSolutionGenerator solutionGenerator, OptimizationProblem problem, MHListener listener) {
        super(title);

        ChartPanel chartPanel = (ChartPanel)createMHChartPanel();
        chartPanel.setPreferredSize(new Dimension(500, 270));
        this.setContentPane(chartPanel);
        runnerThreads =  new ArrayList<>();//new MHThread(alg,problem,this);

        buildAndRunThreads(alg, solutionGenerator,problem,listener);
    }

    private void buildAndRunThreads(AbstractMetaheuristic alg, InitialSolutionGenerator solutionGenerator, OptimizationProblem problem, MHListener listener) {
        ExecutorService threadPool = Executors.newFixedThreadPool(1);
        XYSeries xySeries = new XYSeries(alg.getName());
        dataset.addSeries(xySeries);
        MHThread mt = new MHThread(alg,solutionGenerator,problem,new MHListener(updatePeriod,xySeries));
        mt.addListener(listener);
        runnerThreads.add(mt);
        threadPool.execute(mt);
    }

    private void buildAndRunThreads(List<AbstractMetaheuristic> algs, List<InitialSolutionGenerator> solutionGenerators, OptimizationProblem problem) {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        for (int i=0;i<algs.size();i++)
        {
            XYSeries xySeries = new XYSeries(algs.get(i).getName());
            dataset.addSeries(xySeries);
            MHThread mt = new MHThread(algs.get(i),solutionGenerators.get(i),problem,new MHListener(updatePeriod,xySeries));
            runnerThreads.add(mt);
            threadPool.execute(mt);
        }
    }

    private static JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart("", "Neighboring Count", "Cost", dataset);
        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0D, 5.0D, 5.0D, 5.0D));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYItemRenderer r = plot.getRenderer();
        if(r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer axis = (XYLineAndShapeRenderer)r;
            axis.setBaseShapesVisible(true);
            axis.setBaseShapesFilled(true);
            axis.setDrawSeriesLineAsPath(true);
        }

        //DateAxis axis1 = (DateAxis)plot.getDomainAxis();
        //axis1.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
        return chart;
    }

    private ChartPanel createMHChartPanel() {
        JFreeChart chart = createChart(dataset);

        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    public static void showComparison(String title, List<AbstractMetaheuristic> algs, List<InitialSolutionGenerator> solutionGenerators,  OptimizationProblem problem) {
        MHRunnerPanel demo = new MHRunnerPanel(title,algs,solutionGenerators,problem);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);

        demo.setVisible(true);
    }

    public static void showExecution(String title, AbstractMetaheuristic ea, InitialSolutionGenerator solutionGenerator, OptimizationProblem p, MHListener listener) {
        MHRunnerPanel demo = new MHRunnerPanel(title,ea,solutionGenerator,p,listener);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);

        demo.setVisible(true);
    }

    private static AbstractMetaheuristic createMetaHeuristic1(long maxNeighboringCount)
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimplePermutationCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();

        //PCBProblem pcbProblem = (PCBProblem)problem;

        //muList.add(new GuidedTwoOpt(pcbProblem.getData().getGuidedMap()));

        muList.add(new TwoOpt());
        muList.add(new RandomRemoveReinsert());
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        List<TerminalCondition> tcList= new ArrayList<TerminalCondition>();
        tcList.add(tc1);
        tcList.add(tc2);
        TerminalCondition tc = new OrCompoundTC(tcList);


        EA alg = new EA(coList, muList, ps,tc);

        return alg;
    }
    private static AbstractMetaheuristic createMetaHeuristic2(long maxNeighboringCount )
    {
        DDE alg = new DDE(200,0.5,0.5,new NeighboringBasedTC(maxNeighboringCount),3);

        return alg;
    }



    private static AbstractMetaheuristic createMetaHeuristic62(long maxNeighboringCount )
    {
        PSO alg = new PSO(new DoubleVectorParticleHandler(),0.6031,0.6485,0.6,95,new NeighboringBasedTC(maxNeighboringCount));

        return alg;
    }

    private static AbstractMetaheuristic createMetaHeuristic4(long maxNeighboringCount )
    {
        PCBProblem pcbProblem = (PCBProblem)problem;

        DPSO alg = new DPSO(0.6031,0.6485,0.6,95, new PCBProblemTC(pcbProblem));

        return alg;
    }

    private static AbstractMetaheuristic createMetaHeuristic5(long maxNeighboringCount )
    {
        PCBProblem pcbProblem = (PCBProblem)problem;

        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new MutationWrapperNF(new GuidedTwoOpt(pcbProblem.getData().getGuidedMap())));



        TerminalCondition tcIteration = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tcCost  = new CostBasedTC(0);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcCost,tcIteration));

        TabuSearch alg = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,50);

        return alg;
    }

    private static AbstractMetaheuristic createMetaHeuristic32(long maxNeighboringCount)
    {
        PCBProblem pcbProblem = (PCBProblem)problem;

        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimplePermutationCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        //muList.add(new TwoOpt());
        muList.add(new GuidedTwoOpt(pcbProblem.getData().getGuidedMap()));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1, tc2));

        SOS alg = new SOS(coList, muList, ps,tc);
        alg.setName("Guided-SOS");

        return alg;
    }

    private static AbstractMetaheuristic createMetaHeuristic31(long maxNeighboringCount)
    {
        List<CrossOverOperator> coList= new ArrayList<CrossOverOperator>();
        coList.add(new SimpleMPCrossover());
        //coList.add(new SimplePermutationCrossover());
        List<MutationOperator> muList = new ArrayList<MutationOperator>();
        muList.add(new SimpleMPMutation(0.1));
        //muList.add(new TwoOpt());
        //muList.add(new GuidedTwoOptMutation(p.getData().getGuidedMap()));
        ParentSelector ps = new CostBasedParentSelector(2);

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        //TerminalCondition tc2 = new CostBasedTC(0);
        TerminalCondition tc = new OrCompoundTC(Arrays.asList(tc1));


        SOS alg = new SOS(coList, muList, ps,tc);

        return alg;
    }

    private static AbstractMetaheuristic createMetaHeuristicMBO(long maxNeighboringCount )
    {
        List<MutationOperator> nfList= new ArrayList<>();
        nfList.add(new SimpleMOTAPMutation(1));

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        MBO alg = new MBO(nfList,tc1,51,10,3,1);

        return alg;
    }

    private static AbstractMetaheuristic createMetaHeuristicMBOwithGRMR(long maxNeighboringCount )
    {
        List<MutationOperator> nfList= new ArrayList<>();
        nfList.add(new GreedySwapWithEscape(1));

        TerminalCondition tc1 = new NeighboringBasedTC(maxNeighboringCount);
        MBO alg = new MBO(nfList,tc1,51,10,3,1);

        return alg;
    }

    private static OptimizationProblem createMOTAPProblem() {

        MOTAProblem p = MOTAPGenerator.createRandomProblem(100,16,0.5,2.0);
        return p;
    }

    private static PCBProblem createPCBProblem()
    {
        PCBData pcbData = PCBData.constructPCBData("rPS11AK08-9.txt","./data/realPCB/rPS11AK08-9.txt");
        PCBProblem p = new PCBProblem(pcbData,0);
        return p;
    }

    private static OptimizationProblem createMPProblem()
    {
        MovingPeaks mp = new MovingPeaks(MP_Scenario.DefaultScenario, RandUtil.randInt(1000));
        return mp;
    }


    private static OptimizationProblem createNQProblem()
    {
        return new NQProblem(300);
    }

    public static void main(String[] args) {
        long maxNeighboringCount = 5000;

        AbstractMetaheuristic algMBO = createMetaHeuristicMBO(maxNeighboringCount);
        //AbstractMetaheuristic alg2 = createMetaHeuristic2(maxNeighboringCount);
        AbstractMetaheuristic algMBO_GRMR = createMetaHeuristicMBOwithGRMR(maxNeighboringCount);
        algMBO_GRMR.setName("MBO with GR-MR");
        //AbstractMetaheuristic alg32 = createMetaHeuristic32(maxNeighboringCount);
        //AbstractMetaheuristic alg4 = createMetaHeuristic4(maxNeighboringCount);
        //AbstractMetaheuristic alg5 = createMetaHeuristic5(maxNeighboringCount);
        //AbstractMetaheuristic alg6 = createMetaHeuristic6(maxNeighboringCount);
        AbstractMetaheuristic alg62 = createMetaHeuristic62(maxNeighboringCount);
        List<AbstractMetaheuristic> algs = Arrays.asList(algMBO,algMBO_GRMR);
        List<InitialSolutionGenerator> solutionGenerators = Arrays.asList(new MOTAPRandomSG(),new MOTAPRandomSG(), new MOTAPRandomSG());

        MetaHeuristicListener fileListenerMBO = new FileMetaheuristicListener("MBO_iterations.txt");
        MetaHeuristicListener fileListenerMBO2 = new FileMetaheuristicListener("MBO2_iterations.txt");

        algMBO.addListener(fileListenerMBO);
        algMBO_GRMR.addListener(fileListenerMBO2);

        problem = createMOTAPProblem();
        MHRunnerPanel.showComparison("Metaheuristic Performance Comparison",algs,solutionGenerators,problem);

    }



}
