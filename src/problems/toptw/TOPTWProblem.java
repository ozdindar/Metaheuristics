package problems.toptw;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import problems.base.InitialSolutionGenerator;
import problems.toptw.representation.Tour;
import problems.toptw.representation.TourList;
import representation.PermutationList;
import representation.base.Representation;

import java.util.Vector;

/**
 * Created by dindar.oz on 4.11.2016.
 */
public class TOPTWProblem implements OptimizationProblem {

    public TOPTWData problemData = new TOPTWData();


    InitialSolutionGenerator initialSolutionGenerator;
    CostCalculator costCalculator;

    public TOPTWProblem(TOPTWData toptwData, InitialSolutionGenerator initialSolutionGenerator, CostCalculator costCalculator) {
        this.problemData = toptwData;

        this.initialSolutionGenerator = initialSolutionGenerator;
        this.costCalculator = costCalculator;
    }


    public double tourTime(Vector<Integer> tour)
    {
        double tourTime = Math.max(problemData.distanceMatrix[0][tour.get(0)], problemData.nodes[tour.get(0)].opening);

        for (int i=0;i<tour.size()-1;i++)
        {
            tourTime += problemData.nodes[tour.get(i)].serviceTime;
            tourTime += problemData.distanceMatrix[tour.get(i)][tour.get(i+1)];

            tourTime = Math.max(tourTime, problemData.nodes[tour.get(i+1)].opening);
        }

        tourTime += problemData.nodes[tour.get(tour.lastElement())].serviceTime;
        tourTime += problemData.distanceMatrix[0][tour.lastElement()];
        return tourTime;
    }

    public double profit(Tour tour)
    {
        if (tour.isProfitCalculated())
            return tour.getProfit();

        double profit =0;
        for (int i=0;i<tour.size();i++)
        {
            profit += problemData.nodes[tour.get(i)].profit;
        }
        tour.setProfit(profit);
        return profit;
    }

    public int infeasibilityCount(TourList tl)
    {
        int ic = 0;
        for (Tour t: tl.tours)
        {
            ic += infeasibilityCount(t);
        }
        return ic;
    }

    public int infeasibilityCount(Tour t) {
        if (!t.isTourTimeCalculated()) {
            tourTime(t);
        }
        return t.getInfeasibilityCount();
    }



    public double tourTime(Tour tour)
    {
        if (tour.isTourTimeCalculated())
        {
            return tour.getTourTime();
        }

        int infeasibilityCount =0;

        if (tour.size()==0)
            return 0;

        double tourTime = Math.max(problemData.distanceMatrix[0][tour.get(0)], problemData.nodes[tour.get(0)].opening);
        double waitTime = Math.max(0,problemData.nodes[tour.get(0)].opening- problemData.distanceMatrix[0][tour.get(0)] );
        tour.resetTourTimes();
        for (int i=0;i<tour.size()-1;i++)
        {
            if (problemData.nodes[tour.get(i)].closing<tourTime) {
                infeasibilityCount++;
            }
            tour.setTourTime(i,tourTime,waitTime);

            tourTime += problemData.nodes[tour.get(i)].serviceTime;
            tourTime += problemData.distanceMatrix[tour.get(i)][tour.get(i+1)];

            tourTime = Math.max(tourTime, problemData.nodes[tour.get(i+1)].opening);
            waitTime = Math.max(0, problemData.nodes[tour.get(i+1)].opening-tourTime);
        }
        if (problemData.nodes[tour.lastElement()].closing<tourTime)
        {
            infeasibilityCount++;
        }
        tour.setTourTime(tour.size()-1,tourTime,waitTime);
        tourTime += problemData.nodes[tour.lastElement()].serviceTime;
        tourTime += problemData.distanceMatrix[0][tour.lastElement()];

        tour.setTourTime(tourTime);
        tour.setInfeasibilityCount(infeasibilityCount);
        if (tourTime> problemData.nodes[0].closing)
            infeasibilityCount++;

        tour.setFeasible(infeasibilityCount==0);
        return tourTime;

    }

    public boolean isFeasible(Tour tour)
    {
        if (tour.isFeasibilityCalculated())
        {
            return tour.isFeasible();
        }

        double tourTime = Math.max(problemData.distanceMatrix[0][tour.get(0)], problemData.nodes[tour.get(0)].opening);
        double waitTime = Math.max(0,problemData.nodes[tour.get(0)].opening- problemData.distanceMatrix[0][tour.get(0)] );
        tour.resetTourTimes();
        for (int i=0;i<tour.size()-1;i++)
        {
            if (problemData.nodes[tour.get(i)].closing<tourTime) {
                tour.setFeasible(false);
                return false;
            }
            tour.setTourTime(i,tourTime,waitTime);

            tourTime += problemData.nodes[tour.get(i)].serviceTime;
            tourTime += problemData.distanceMatrix[tour.get(i)][tour.get(i+1)];

            tourTime = Math.max(tourTime, problemData.nodes[tour.get(i+1)].opening);
            waitTime = Math.max(0, problemData.nodes[tour.get(i+1)].opening-tourTime);
        }
        if (problemData.nodes[tour.lastElement()].closing<tourTime)
        {
            tour.setFeasible(false);
            return false;
        }

        tour.setTourTime(tour.size()-1,tourTime,waitTime);
        tourTime += problemData.nodes[tour.lastElement()].serviceTime;
        tourTime += problemData.distanceMatrix[0][tour.lastElement()];

        tour.setTourTime(tourTime);
        return tourTime<= problemData.nodes[0].closing;
    }

    @Override
    public boolean isFeasible(Representation i) {
        if (!(i instanceof PermutationList))
            throw new InvalidIndividual("TOPTW accepts only PermutationList");

        TourList tours = (TourList) i;
        if (tours.tourCount()> problemData.m)
            return false;

        for (Tour tour:tours.tours)
        {
            if (!isFeasible(tour))
                return false;
        }
        return true;
    }

    @Override
    public double cost(Representation i) {
        return costCalculator.calculateCost(this,i);
    }

    @Override
    public double maxDistance() {
        return 0;
    }



    public int nodeCount() {
        return problemData.nodes.length-1;
    }

    public double profit(TourList tl) {
        double profit =0;
        for (Tour tour:tl.tours)
        {
            profit += profit(tour);
        }
        return profit;
    }

    public double maxProfit() {
        double maxProfit = problemData.nodes[0].profit;

        for (int i = 1; i< problemData.nodes.length; i++)
        {
            if (maxProfit < problemData.nodes[i].profit)
                maxProfit = problemData.nodes[i].profit;
        }
        return maxProfit;
    }
}
