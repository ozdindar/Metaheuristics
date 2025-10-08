package metrics.stn;

import base.OptimizationProblem;
import base.TerminalCondition;
import javafx.util.Pair;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.IterationEvent;

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
import problems.base.InitialSolutionGenerator;
import problems.bbob.BBOBProblem;
import problems.bbob.BBOBRandomISG;
import problems.bbob.f1_Sphere.Sphere;
import problems.bbob.mutation.BBOBOneCutCO;
import problems.bbob.mutation.BBOBPerturbator;
import problems.bbob.mutation.BBOBRandomShift;

import metrics.partitioning.DoubleVectorPartitioner;
import metrics.partitioning.SearchSpacePartitioner;
import representation.base.Representation;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class STNBuilder implements DataBuilder<STN> {

    OptimizationProblem problem;
    SearchSpacePartitioner ssp;

    STN stn;
    STNNode previous;
    Representation previousRep;
    private boolean trackGlobalBest= false;

    public STNBuilder() {

        stn = new STN();
        previous = null;

    }

    @Override
    public STN getData() {
        return stn;
    }

    public void init(OptimizationProblem problem, SearchSpacePartitioner ssp)
    {
        this.problem= problem;
        this.ssp = ssp;
        stn = new STN();
        previous = null;
        previousRep=null;
    }

    @Override
    public void updateData(SearchSpacePartitioner ssp) {
        stn = buildFromExisting(stn,ssp);
    }

    public void setTrackGlobalBest(boolean trackGlobalBest) {
        this.trackGlobalBest = trackGlobalBest;
    }

    @Override
    public void onIterationEvent(IterationEvent event) {
        stn.newIteration();
        Representation rep = trackGlobalBest ? event.getBestSolution():event.getCurrentSolution();
        double cost = trackGlobalBest ? event.getBestCost(): event.getCurrentCost() ;

        stn.updateBest(rep,cost);


        String id = ssp.idOf(rep);
        STNNode node = stn.getNode(id,rep,cost);

        if (previous == null) {
            previous = node;
        }

        if (  previousRep != null && previousRep.equals(rep))
            previous.stall();
        else if (node.equals(previous)) // point changed but still the same location
        {
            previous.stay(rep, cost, stn.iteration());
            previousRep = rep;
        }else { // Location changed
            previous.traverse(node, rep, cost,stn.iteration());
            previous = node;
            previousRep = rep;
        }
    }

    public STN getSTN() {
        return stn;
    }

    /**
     * ! It assumes that the partitioning (ssp) is more general than the existing
     * ! such that every solution in one node in the existing will be in the same node
     * ! in the new one
     * @param stn
     * @param ssp
     * @return
     */
    public static STN buildFromExisting(STN stn, SearchSpacePartitioner ssp)
    {
        Collection<STNNode> nodes = stn.getNodes();
        STN newSTN = new STN();
        newSTN.bestRep = stn.getBestRep();
        newSTN.bestCost= stn.getBestCost();
        newSTN.iteration = stn.iteration();

        for (STNNode node:nodes)
        {
            String newID= ssp.idOf(node.getbRep());
            STNNode target = newSTN.getNode(newID, node.bRep, node.bCost);
            mergeNodeInto(node,target);
        }
        for (STNNode node:nodes)
        {
            String newID= ssp.idOf(node.getbRep());
            STNNode target = newSTN.getNode(newID, node.bRep, node.bCost);
            mergeTraversesInto(node,target,newSTN,ssp);
        }

        return newSTN;
    }

    private static void mergeTraversesInto(STNNode node, STNNode target, STN newSTN, SearchSpacePartitioner ssp) {
        for (STNNode neighbor: node.neighborMap.keySet())
        {
            STNEdge edge = node.neighborMap.get(neighbor);
            STNNode newNeighbor = newSTN.getNode(ssp.idOf(neighbor.getbRep()), neighbor.bRep, neighbor.bCost);
            if (newNeighbor.equals(target))
            {
                target.totalStay += edge.weight;
                // TODO: There is something to be solved about maxStays?!
            }
            else {
                target.neighborMap.putIfAbsent(newNeighbor,new STNEdge(target,newNeighbor,0.0));
                STNEdge newEdge= target.neighborMap.get(newNeighbor);
                newEdge.weight += edge.weight;
            }
        }
    }

    private static void mergeNodeInto(STNNode src, STNNode target) {
        target.updateBest(src.bRep,src.bCost);
        target.visitCount += src.visitCount;
        target.totalStay += src.totalStay;
        target.totalStall += src.totalStall;
        target.maxStay = Math.max(target.maxStay,src.maxStay);
        target.maxStall = Math.max(target.maxStall,src.maxStall);
        target.firstVisit = target.firstVisit== 0 ? src.firstVisit : Math.min(target.firstVisit,src.firstVisit);
        target.lastVisit = target.lastVisit==0 ? src.lastVisit : Math.min(target.lastVisit,src.lastVisit);
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
    private static AbstractMetaheuristic createSA()
    {
        MutationOperator mo = new BBOBRandomShift(0.1);
        SA sa = new SA(Arrays.asList(mo),new LineerCooling(0.001),10,10000,0.0);
        return sa;
    }


    private static AbstractMetaheuristic createRW()
    {
        TerminalCondition tc = new IterationBasedTC(1000);
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


        AbstractMetaheuristic alg = createSA();

        BBOBProblem problem = new Sphere(3);
        double[] ubounds = IntStream.range(0,problem.getDimension()).mapToDouble(problem::upperBound).toArray();
        double[] lbounds = IntStream.range(0,problem.getDimension()).mapToDouble(problem::lowerBound).toArray();
        int slotCount =3;
        DoubleVectorPartitioner ssp = new DoubleVectorPartitioner(ubounds,lbounds,slotCount);

        InitialSolutionGenerator isg = new BBOBRandomISG();

        STNBuilder stnBuilder = new STNBuilder();
        stnBuilder.init((OptimizationProblem) problem,ssp);
        alg.addListener(stnBuilder);
        alg.perform((OptimizationProblem) problem,isg);

        STN stn = stnBuilder.getSTN();
        Predicate<STNNode> filter = (x)->x.firstVisit>0;

        System.out.println("Constructing visualizer..");

        STNUtils.printStats(stn, problem.getDimension(), filter);

        STNVisualizer.visualize(stn, filter);

        Map<String, Function<STNNode,Number>> extractors = new HashMap<>();
        extractors.put("OutWeight", STNNode::totalOutgoingEdgeWeight);
        extractors.put("InWeight", stn::totalInWeight);

        HistogramPlotter.createMultiHistogram(stn.nodeMap,extractors,"./output/In&OutWeight of Nodes","NodeID","IOWeights","Histogram-OutWeight.png");

    }


    private static void demo3Graphs() {


        AbstractMetaheuristic alg = createSA();

        BBOBProblem problem = new Sphere(3);
        double[] ubounds = IntStream.range(0,problem.getDimension()).mapToDouble(problem::upperBound).toArray();
        double[] lbounds = IntStream.range(0,problem.getDimension()).mapToDouble(problem::lowerBound).toArray();
        int slotCount =5;
        DoubleVectorPartitioner ssp = new DoubleVectorPartitioner(ubounds,lbounds,slotCount);



        InitialSolutionGenerator isg = new BBOBRandomISG();

        STNBuilder stnBuilder = new STNBuilder();
        stnBuilder.init((OptimizationProblem) problem,ssp);
        alg.addListener(stnBuilder);
        alg.perform((OptimizationProblem) problem,isg);

        Predicate<STNNode> filter10 = (x)->x.firstVisit>1000;
        Predicate<STNNode> filter30 = (x)->x.firstVisit>3000;
        Predicate<STNNode> filter50 = (x)->x.firstVisit>5000;
        Predicate<STNNode> filter70 = (x)->x.firstVisit>7000;
        Predicate<STNNode> filter90 = (x)->x.firstVisit>9000;

        STN stn = stnBuilder.getSTN();

        List<Pair<String,Predicate<STNNode>>> displays =
                Arrays.asList(  new Pair<>("./output/stnOverall.png",null),
                                new Pair<>("./output/stn10.png",filter10),
                                new Pair<>("./output/stn30.png",filter30),
                                new Pair<>("./output/stn50.png",filter50),
                                new Pair<>("./output/stn70.png",filter70),
                                new Pair<>("./output/stn90.png",filter90) );
        // Overall
        STNUtils.printStats(stn, problem.getDimension());

        STNVisualizer.visualize(stn,displays);

    }

    public static void main(String[] args) {
        demo();
        //demoWithSA();
    }

}
