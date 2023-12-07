package problems.toptw.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.toptw.TOPTWProblem;
import problems.toptw.representation.Tour;
import problems.toptw.representation.TourList;
import representation.base.Representation;
import util.random.RandUtil;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class SmartRemoveNode implements MutationOperator {
    private double smartnessRate;

    public SmartRemoveNode(double smartnessRate) {
        this.smartnessRate = smartnessRate;
    }

    @Override
    public Representation apply(OptimizationProblem problem, Representation i) {
        TourList tl = (TourList) i;
        TourList ntl = (TourList) tl.clone();

        TOPTWProblem toptwProblem = (TOPTWProblem)problem;

        int node2Remove;
        if (toptwProblem.infeasibilityCount(ntl)>0 && RandUtil.rollDice(smartnessRate))
        {
            removeWorst(toptwProblem,ntl);
        }
        else
        {
            removeRandomNode(toptwProblem,ntl);
        }

        return ntl;
    }

    @Override
    public int neighboringCount() {
        return 1;
    }

    private void removeWorst(TOPTWProblem toptwProblem, TourList ntl) {
        int tour2Remove = maxInfeasible(toptwProblem,ntl);

        Tour tour = ntl.get(tour2Remove);
        if (tour.isEmpty())
            return;

        int node2Remove = maxInfeasible(toptwProblem,ntl.get(tour2Remove));

        tour.remove(node2Remove);
    }

    private int maxInfeasible(TOPTWProblem toptwProblem, Tour tour) {
        for (int i=0; i<tour.size();i++)
        {
            if (tour.getTourTime(i)>toptwProblem.problemData.nodes[tour.get(i)].closing)
                return i;
        }
        return tour.size()-1;
    }

    private int maxInfeasible(TOPTWProblem toptwProblem, TourList ntl) {
        int maxTour = 0;
        int max = toptwProblem.infeasibilityCount(ntl.get(0));

        for (int i=0;i<ntl.tourCount();i++)
        {
            int ic = toptwProblem.infeasibilityCount(ntl.get(i));
            if (ic >max )
            {
                max = ic;
                maxTour = i;
            }
        }
        return maxTour;
    }

    private void removeRandomNode(TOPTWProblem toptwProblem, TourList ntl) {

        int tour = RandUtil.randInt(ntl.tourCount());

        if (ntl.get(tour).isEmpty())
            return;

        int n1 = RandUtil.randInt(ntl.get(tour).size());

        ntl.removeNode(tour,n1);
    }


}
