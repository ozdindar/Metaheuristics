package metaheuristic.gls;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidProblem;
import metaheuristic.AbstractSMetaheuristic;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;


/**
 * Created by dindar.oz on 21.07.2016.
 */
public class GLS extends AbstractSMetaheuristic {
    private int iterationCount =0;

    double lamda;

    AbstractSMetaheuristic localSearch;
    TerminalCondition terminalCondition;
    private double[] utility;


    @Override
    public String defaultName() {
        return "GLS";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }



    @Override
    public void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        if (!(problem instanceof GLSProblem))
            throw new InvalidProblem("The problem is not a GLS problem");
        GLSProblem glsProblem = (GLSProblem) problem;

        int featureCount =_init(glsProblem);

        while (!terminalCondition.isSatisfied(this,problem))
        {
            localSearch.setCurrentSolution(currentSolution);
            localSearch.perform(problem,solutionGenerator);
            Representation bestSolution = localSearch.getBestKnownSolution();

            int maxUtilityFeature = calculateUtilities(glsProblem,bestSolution);

            glsProblem.setFeaturePenalty(maxUtilityFeature,glsProblem.getFeaturePenalty(maxUtilityFeature)+1);



        }





    }

    @Override
    public AbstractSMetaheuristic clone() {
        throw new RuntimeException("Not implemented");
    }

    private int calculateUtilities(GLSProblem glsProblem,Representation r) {
        int featureCount = glsProblem.getFeatureCount();
        double maxUtility =0;
        int maxUtilityIndex =-1;
        for (int i=0; i<featureCount; i++)
        {
            if (!glsProblem.hasFeature(r,i))
                continue;
            utility[i] = (double) glsProblem.getFeatureCost(i) / (double)(1+glsProblem.getFeaturePenalty(i));
            if (maxUtility<utility[i])
            {
                maxUtility = utility[i];
                maxUtilityIndex =i;
            }
        }
        return maxUtilityIndex;
    }

    private int _init(GLSProblem glsProblem) {
        int featureCount = glsProblem.getFeatureCount();

        for (int i=0;i<featureCount ;i++)
        {
            glsProblem.setFeaturePenalty(i,0);
        }
        iterationCount =0;

        utility = new double[featureCount];

        return featureCount;

    }
}
