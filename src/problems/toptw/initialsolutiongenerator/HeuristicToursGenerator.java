package problems.toptw.initialsolutiongenerator;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import problems.toptw.TOPTWProblem;
import problems.toptw.representation.Tour;
import problems.toptw.representation.TourList;
import representation.base.Representation;
import util.random.RNG;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class HeuristicToursGenerator implements InitialSolutionGenerator {


    private double fullnessRate;

    public HeuristicToursGenerator(double fullnessRate) {
        this.fullnessRate = fullnessRate;
    }

    Representation generate(TOPTWProblem toptwProblem, RNG rng)
    {
        RandUtil.setRNG(rng);
        Vector<Tour> tours = new Vector<>(toptwProblem.problemData.m);
        Vector<Integer> initialNodes = createInitialNodes(toptwProblem.nodeCount());

        for (int i = 0; i< toptwProblem.problemData.m; i++) {
            Tour tour = new Tour(new Vector<>());
            int node = initialNodes.get(RandUtil.randInt(initialNodes.size()));

            tour.add(node);
            initialNodes.removeElement(node);

            while (true)
            {
                int newNode = selectBest(toptwProblem,node,initialNodes);
                if (newNode ==-1)
                    break;
                tour.add(newNode);
                if (toptwProblem.isFeasible(tour))
                {
                    initialNodes.removeElement(newNode);
                    node = newNode;
                } else
                {
                    tour.remove(tour.size()-1);
                    break;
                }
            }
            tours.add(tour);
        }


        TourList tl =new TourList(tours);
        return tl;
    }

    private int selectBest(TOPTWProblem toptwProblem, int node, Vector<Integer> nodePool) {
        int bestNode = -1;
        double bestValue = Double.MIN_VALUE;


        for (int i=1;i<nodePool.size();i++)
        {
            /*todo: Select best in terms of a heuristic*/
            int nextNode = nodePool.get(i);

        }
        return bestNode;
    }

    private Vector<Integer> createInitialNodes(int size) {
        Vector<Integer> nodes = new Vector<>(size);
        for (int i =0; i<size ; i++)
            nodes.add(i+1);

        return nodes;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {

        List<Representation> list = new ArrayList<>(c);
        for (int i = 0; i < c; i++) {
            list.add(generate((TOPTWProblem)problem,rng));
        }

        return list;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, RandUtil.getDefaultRNG(),c);
    }
}
