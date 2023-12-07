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
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class RandomToursGenerator implements InitialSolutionGenerator {


    private double fullnessRate;

    public RandomToursGenerator(double fullnessRate) {
        this.fullnessRate = fullnessRate;
    }

    Representation generate(TOPTWProblem toptwProblem, RNG rng)
    {
        RandUtil.setRNG(rng);
        Vector<Integer> initialNodes = createInitialNodes(toptwProblem.nodeCount());
        Collections.shuffle(initialNodes);
        int tourSize = initialNodes.size()/ toptwProblem.problemData.m;

        Vector<Tour> tours = new Vector<>(toptwProblem.problemData.m);

        for (int i = 0; i< toptwProblem.problemData.m; i++)
        {
            Vector<Integer> tour = new Vector<>();

            int nodeCount =  (i == (toptwProblem.problemData.m -1))? tourSize:tourSize + ( toptwProblem.nodeCount()% toptwProblem.problemData.m);
            for (int j = 0 ;j<nodeCount; j++)
            {
                if (RandUtil.rollDice(fullnessRate))
                    tour.add( initialNodes.get(i*tourSize+j));
            }


            tours.add(new Tour(tour));
        }
        TourList tl =new TourList(tours);
        return tl;
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
        list.add(generate((TOPTWProblem)problem,rng));
        return list;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, RandUtil.getDefaultRNG(),c);
    }
}
