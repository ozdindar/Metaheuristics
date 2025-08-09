package experiments;

import base.OptimizationProblem;
import base.TerminalCondition;
import experiments.datacollectors.phm.TouchNodeCountDC;
import experiments.datacollectors.phm.VisitNodeCountDC;
import experiments.datacollectors.stn.*;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.de.*;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.parentselector.RouletteWheelParentSelector;
import metaheuristic.ea.terminalcondition.IterationBasedTC;
import metaheuristic.pso.DoubleVectorParticleHandler;
import metaheuristic.pso.PSO;
import metrics.DataBuilder;
import metrics.DataCollector;
import metrics.partitioning.DoubleVectorPartitioner;
import metrics.partitioning.SearchSpacePartitioner;
import metrics.populationheatmap.PHMBuilder;
import metrics.stn.STNBuilder;
import problems.base.InitialSolutionGenerator;
import problems.bbob.BBOBRandomISG;
import problems.bbob.f1_Sphere.Sphere;
import problems.bbob.mutation.BBOBOneCutCO;
import problems.bbob.mutation.BBOBRandomShift;
import util.Pair;

import java.util.Arrays;
import java.util.List;

public class Experiment {

    final List<ExperimentCase> cases;

    final int repeatCount;

    final List<Pair<DataBuilder, List<DataCollector>>> dataCollectors;
    final List<ExperimentResultHandler> resultHandlers;


    public Experiment(List<ExperimentResultHandler> resultHandlers, int repeatCount, List<ExperimentCase> cases, List<Pair<DataBuilder, List<DataCollector>>> dataCollectors) {
        this.resultHandlers = resultHandlers;
        this.repeatCount = repeatCount;
        this.cases = cases;

        this.dataCollectors = dataCollectors;
    }

    private void runOnce( ExperimentCase experimentCase) {

        dataCollectors.forEach(p->p.first.init(experimentCase.getProblem(),experimentCase.getSsp()));

        experimentCase.getAlg().perform((OptimizationProblem) experimentCase.getProblem(), experimentCase.getIsg());

        for (Pair<DataBuilder,List<DataCollector>> builderPair: dataCollectors)
        {
            builderPair.second.forEach(dc->dc.collect(builderPair.first.getData()));
        }
    }

    public void run()
    {
        for (ExperimentCase experimentCase: cases)
        {
            initDataCollectors(experimentCase);
            dataCollectors.forEach(p-> experimentCase.getAlg().addListener(p.first));

            for (int repeat = 0; repeat < repeatCount; repeat++) {
                System.out.println("EC: "+ experimentCase + "  RC:"+ repeat);
                runOnce(experimentCase);
            }
            String resultStr = buildResult(experimentCase);
            resultHandlers.forEach(erh-> erh.handle(resultStr));
        }
    }

    private void initDataCollectors(ExperimentCase experimentCase) {
        for (Pair<DataBuilder,List<DataCollector>> builderPair: dataCollectors)
        {
            builderPair.second.forEach(dc->dc.init(experimentCase));
        }
    }

    private String buildResult(ExperimentCase experimentCase) {
        StringBuilder resultStr = new StringBuilder(String.format("%20s",experimentCase.getTitle())+ " ");
        for (Pair<DataBuilder,List<DataCollector>> builderPair: dataCollectors)
        {
            for (DataCollector dc:builderPair.second) {
                String st = dc.resultAsString();
                if (!st.isEmpty())
                    resultStr.append(" " + String.format("%20s",dc.resultAsString()));
            }

        }
       return resultStr.append("\n").toString();
    }


    static SearchSpacePartitioner bbobPartitioner(int dimension)
    {
        int slotCount= 5;
        double[] lBounds = new double[dimension];
        double[] uBounds = new double[dimension];
        Arrays.fill(lBounds,-5.0);
        Arrays.fill(uBounds,5.0);
        SearchSpacePartitioner ssp = new DoubleVectorPartitioner(uBounds,lBounds,slotCount);
        return ssp;
    }

    private static AbstractMetaheuristic createGA() {
        MutationOperator mo = new BBOBRandomShift();
        CrossOverOperator co = new BBOBOneCutCO();
        TerminalCondition tc = new IterationBasedTC(10000);

        EA ea = new EA(Arrays.asList(co),
                Arrays.asList(mo),
                new RouletteWheelParentSelector(2),
                tc);

        return ea;
    }

    private static AbstractMetaheuristic createPSO() {
        TerminalCondition tc = new IterationBasedTC(10000);
        PSO pso = new PSO(new DoubleVectorParticleHandler()
                ,0.5034, 1.0519, 2.3403, // W, C1, C2
                50, // Swarm Size
                tc);
        return pso;
    }


    private static AbstractMetaheuristic createDE(BoundaryHandler bh) {
        TerminalCondition tc = new IterationBasedTC(10000);

        AbstractMetaheuristic de = new DifferentialEvolution(bh,tc,50,0.9,0.8);
        return de;
    }

    static List<ExperimentCase> createDE_Boundary_Cases(OptimizationProblem problem,
                                                      InitialSolutionGenerator isg,
                                                      SearchSpacePartitioner ssp)
    {
        DifferentialEvolution de_sat = (DifferentialEvolution) createDE(new SaturationBH());
        DifferentialEvolution de_mir = (DifferentialEvolution) createDE(new MirroringBH());
        DifferentialEvolution de_rnd = (DifferentialEvolution) createDE(new RandomBH());
        DifferentialEvolution de_mid = (DifferentialEvolution) createDE(new MidpointBH());

        AbstractMetaheuristic pso =createPSO();


        return Arrays.asList(
                    new ExperimentCase("DE-Saturation" ,ssp,de_sat,problem,isg),
                    new ExperimentCase("DE-Mirror" ,ssp,de_mir,problem,isg),
                    new ExperimentCase("DE-Midpoint" ,ssp,de_mid,problem,isg),
                    new ExperimentCase("DE-Random" ,ssp,de_rnd,problem,isg),
                    new ExperimentCase("PSO" ,ssp,pso,problem,isg)
                );
    }




    static void bbobExperiment()
    {
        int dimension = 20;
        SearchSpacePartitioner ssp = bbobPartitioner(dimension);
        InitialSolutionGenerator isg = new BBOBRandomISG();
        OptimizationProblem problem = new Sphere(dimension);//new AttractiveSector(dimension,new Random(42));

        List<ExperimentCase> ecList= createDE_Boundary_Cases(problem,isg,ssp);

        List<DataCollector> stnCollectors = Arrays.asList(
                new BestCostDC(),
                new HyperVolumeIndexDC(dimension, ssp.locationCount()),
                new HyperVolumeDC(dimension),
                new NodeCountDC(),
                new ConstantDC("LocCnt",ssp.locationCount()),
                new StayRateDC(),
                new StayRateOfMostDC(),
                new StayRateOfBestDC(),
                new VisitRateMostDC(),
                new StallRateDC()
        );

        List<DataCollector> phmCollectors = Arrays.asList(new VisitNodeCountDC(),new TouchNodeCountDC());


        List<Pair<DataBuilder,List<DataCollector>>> builderList = Arrays.asList(
                new Pair<>(new STNBuilder(),stnCollectors),
                new Pair<>(new PHMBuilder(),phmCollectors)
        );

        List<ExperimentResultHandler> resultHandlers = Arrays.asList(
                new TextFileERH("./output/exp/exp_de_pso.txt")
        );

        Experiment experiment = new Experiment(resultHandlers,50,ecList,builderList);

        experiment.run();

    }

    private static List<ExperimentCase> createPSO(SearchSpacePartitioner ssp, OptimizationProblem problem, InitialSolutionGenerator isg) {
        AbstractMetaheuristic alg =createPSO();
        return Arrays.asList(new ExperimentCase("PSO",ssp,alg,problem,isg));
    }


    public static void main(String[] args) {
        bbobExperiment();
    }
}
