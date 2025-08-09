package problems.toptw.crossover;

import base.OptimizationProblem;
import exceptions.InvalidIndividual;
import exceptions.InvalidProblem;
import metaheuristic.ea.base.CrossOverOperator;
import problems.toptw.TOPTWProblem;
import problems.toptw.representation.TourList;
import representation.base.Representation;

import java.util.Arrays;
import java.util.List;

/**
 * Created by oz on 16.07.2015.
 */
public class SimpleTOPTWCrossOver implements CrossOverOperator
{
    @Override
    public List<Representation> apply(OptimizationProblem problem, Representation p1, Representation p2) {
        if (!(problem instanceof TOPTWProblem))
            throw new InvalidProblem("Works only for TOPTW Problems");

        if (!(p1 instanceof TourList))
            throw new InvalidIndividual("Works only for TourList");

        if (!(p2 instanceof TourList))
            throw new InvalidIndividual("Works only for TourList");

        TOPTWProblem toptwProblem = (TOPTWProblem) problem;

        TourList offspring1 = (TourList) p1.clone();
        TourList offspring2 = (TourList) p2.clone();


        if (toptwProblem.problemData.m <=1)
            return Arrays.asList(offspring1,offspring2) ;



        List<Representation> offsprings = Arrays.asList(offspring1,offspring2);
        return offsprings;
    }
}
