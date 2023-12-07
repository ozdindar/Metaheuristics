package metaheuristic;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;

/**
 * Created by dindar.oz on 17.06.2015.
 */
public interface MetaHeuristic {
    public String generateResultString();
    public int getIterationCount();
    public long getNeighboringCount();
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator);
    public void init(OptimizationProblem problem);

    public double getBestAchieveTime();
    public double getBestKnownCost();
    public Representation getBestKnownSolution();

    public String getName();
    public void setName(String name);


    void setDebugTrace(boolean debugTrace);
}
