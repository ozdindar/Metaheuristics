package problems.toptw.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.toptw.TOPTWProblem;
import problems.toptw.representation.TourList;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.Vector;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class AddNode implements MutationOperator {
    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
        TourList tl = (TourList) i;
        TourList ntl = (TourList) tl.clone();
        TOPTWProblem toptwProblem = (TOPTWProblem) problem;

        Vector<Integer> absentList = createAbsentList(tl,toptwProblem.nodeCount());

        if (absentList.isEmpty())
            return ntl;
        int tour = RandUtil.randInt(tl.tourCount());

        int n1 = RandUtil.randInt(absentList.size());

        ntl.addNode(tour,absentList.get(n1));
        return ntl;
    }

    @Override
    public int neighboringCount() {
        return 1;
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
