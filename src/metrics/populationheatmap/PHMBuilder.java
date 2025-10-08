package metrics.populationheatmap;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.IterationEvent;

import metaheuristic.MetaHeuristicListener;
import metaheuristic.PIterationEvent;
import metaheuristic.ea.EA;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.parentselector.RouletteWheelParentSelector;
import metaheuristic.ea.terminalcondition.IterationBasedTC;
import metaheuristic.ils.ILS;
import metaheuristic.randomwalk.RandomWalk;
import metaheuristic.sa.LineerCooling;
import metaheuristic.sa.SA;
import metrics.DataBuilder;
import metrics.HistogramPlotter;
import metrics.partitioning.DoubleVectorPartitioner;
import metrics.partitioning.SearchSpacePartitioner;
import problems.base.InitialSolutionGenerator;
import problems.bbob.BBOBProblem;
import problems.bbob.BBOBRandomISG;
import problems.bbob.f1_Sphere.Sphere;
import problems.bbob.mutation.BBOBOneCutCO;
import problems.bbob.mutation.BBOBPerturbator;
import problems.bbob.mutation.BBOBRandomShift;
import representation.base.Population;
import representation.base.Representation;
import tmp.PHMPlotter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class PHMBuilder implements DataBuilder<PHM> {

    OptimizationProblem problem;
    SearchSpacePartitioner ssp;

    PHM phm;
    Set<PHMNode> previousNodes;


    public PHMBuilder() {

        phm = new PHM();
        previousNodes = new HashSet<>();

    }

    @Override
    public PHM getData() {
        return phm;
    }

    public void init(OptimizationProblem problem, SearchSpacePartitioner ssp)
    {
        this.problem= problem;
        this.ssp = ssp;
        phm = new PHM();
        previousNodes.clear();
    }

    @Override
    public void updateData(SearchSpacePartitioner ssp) {
        phm = buildFromExisting(phm,ssp);
    }

    @Override
    public void onIterationEvent(IterationEvent event) {
        assert event instanceof  PIterationEvent : "PHM can be built by only PIterationEvent";
        PIterationEvent pEvent = (PIterationEvent) event;
        phm.newIteration();
        Representation rep =  pEvent.getBestSolution();
        double cost =  pEvent.getBestCost();

        phm.updateBest(rep,cost);

        Population population= pEvent.getCurrentPopulation();

        phm.update(population, ssp);


    }

    public PHM getPHM() {
        return phm;
    }

    /**
     * ! It assumes that the partitioning (ssp) is more general than the existing
     * ! such that every solution in one node in the existing will be in the same node
     * ! in the new one
     * @param phm
     * @param ssp
     * @return
     */
    public static PHM buildFromExisting(PHM phm, SearchSpacePartitioner ssp)
    {
        Collection<PHMNode> nodes = phm.getNodes();
        PHM newPHM = new PHM();
        newPHM.bestRep = phm.getBestRep();
        newPHM.bestCost= phm.getBestCost();
        newPHM.iteration = phm.iteration();

        for (PHMNode node:nodes)
        {
            String newID= ssp.idOf(node.getbRep());
            PHMNode target = newPHM.getNode(newID);
            mergeNodeInto(node,target);
        }

        return newPHM;
    }

    private static void mergeNodeInto(PHMNode node, PHMNode target) {
        // TODO: how to differentiate touches and visits. looks impossible!
    }


    private static AbstractMetaheuristic createGA() {
        MutationOperator mo = new BBOBRandomShift();
        CrossOverOperator co = new BBOBOneCutCO();
        TerminalCondition tc = new IterationBasedTC(100000);

        EA ea = new EA(Arrays.asList(co),
                Arrays.asList(mo),
                new RouletteWheelParentSelector(2),
                tc);

        return ea;
    }
    private static AbstractMetaheuristic createSA()
    {
        MutationOperator mo = new BBOBRandomShift(0.1);
        SA sa = new SA(Arrays.asList(mo),new LineerCooling(0.001),10,10000,0.0);
        return sa;
    }


    private static AbstractMetaheuristic createRW()
    {
        TerminalCondition tc = new IterationBasedTC(10);
        InitialSolutionGenerator isg = new BBOBRandomISG();
        AbstractMetaheuristic rw = new RandomWalk(isg,tc);
        return rw;
    }

    private static AbstractMetaheuristic createILS()
    {
        MutationOperator mo = new BBOBRandomShift();
        TerminalCondition tc = new IterationBasedTC(1000);
        SA sa1 = new SA(Arrays.asList(mo),new LineerCooling(0.001),10,100,0.0);

        AbstractMetaheuristic ils1 = new ILS(sa1,new BBOBPerturbator(0.5,3.0),tc);

        return ils1;

    }




    private static void demo() {


        AbstractMetaheuristic alg = createGA();

        BBOBProblem problem = new Sphere(20);
        double[] ubounds = IntStream.range(0,problem.getDimension()).mapToDouble(problem::upperBound).toArray();
        double[] lbounds = IntStream.range(0,problem.getDimension()).mapToDouble(problem::lowerBound).toArray();
        int slotCount =10;
        DoubleVectorPartitioner ssp = new DoubleVectorPartitioner(ubounds,lbounds,slotCount);

        InitialSolutionGenerator isg = new BBOBRandomISG();

        PHMBuilder phmBuilder = new PHMBuilder();
        phmBuilder.init((OptimizationProblem) problem,ssp);
        alg.addListener(phmBuilder);
        alg.perform((OptimizationProblem) problem,isg);

        PHM phm = phmBuilder.getPHM();
        Predicate<PHMNode> filter = (x)->x.firstVisit>0;

        System.out.println("Constructing visualizer..");

/*
        HistogramPlotter.createMultiHistogram(
                                phm.nodeMap,
                                extractors,
                                "Visit&Touch per Node",
                                "NodeID",
                                "VC-TC",
                                "./output/Histogram-VC-TC.png");*/

        PHMPlotter.plotVisitedNodesBetweenIterations(phm,0.01);
    }


    public static void main(String[] args) {
        demo();
        //demoWithSA();
    }

}
