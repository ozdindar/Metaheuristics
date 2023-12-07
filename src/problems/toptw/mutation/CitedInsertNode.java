package problems.toptw.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.toptw.Node;
import problems.toptw.TOPTWProblem;
import problems.toptw.representation.Tour;
import problems.toptw.representation.TourList;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.Vector;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class CitedInsertNode implements MutationOperator {
    private double smartnessRate;

    public CitedInsertNode(double smartnessRate) {
        this.smartnessRate = smartnessRate;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
        TOPTWProblem toptwProblem = (TOPTWProblem) problem;

        TourList tl = (TourList) i;
        TourList ntl = (TourList) tl.clone();

        Vector<Integer> absentList = createAbsentList(tl,toptwProblem.nodeCount());
        if (absentList.isEmpty())
            return ntl;

        double profits[] = getProfits(toptwProblem.problemData.nodes,absentList);


        int tour = RandUtil.randInt(tl.tourCount());

        int node = absentList.get(RandUtil.rouletteSelect(profits));


        int insertPoint = findInsertPoint(toptwProblem,tl.get(tour),node);

        if (insertPoint == -1 || insertPoint>tl.get(tour).size() )
            return ntl;
        else if (insertPoint == tl.get(tour).size())
            ntl.addNode(tour,node);

        else ntl.insert(tour,node,insertPoint);
        return ntl;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private int findInsertPoint(TOPTWProblem toptwProblem, Tour tour, Integer node) {

        double tourTime = toptwProblem.tourTime(tour);
        double closing = toptwProblem.problemData.nodes[node].closing;
        for (int i =0; i<tour.size();i++)
        {
            double currentTourTime = tour.getTourTime(i);
            if (currentTourTime>closing)
                return i-1;
        }
        return tour.size();
    }

    private double[] getProfits(Node[] allNodes, Vector<Integer> nodeList) {
        double profits[] = new double[nodeList.size()];
        for (int i=0; i<nodeList.size(); i++)
        {
            profits[i] = allNodes[nodeList.get(i)].profit;
        }
        return profits;
    }

    private Vector<Integer> createAbsentList(TourList tl, int nodeCount) {
        Vector<Integer> absentList = new Vector<>();
        for (int i=1; i<=nodeCount; i++)
        {
            if (!tl.contains(i))
                absentList.add(i);
        }
        return absentList;
    }
}
