package metaheuristic.aco;

import base.OptimizationProblem;
import metaheuristic.AbstractMetaheuristic;
import problems.base.InitialSolutionGenerator;

/**
 * Created by dindar.oz on 3.01.2017.
 */
public class ACO extends AbstractMetaheuristic {
    private int iterationCount;

    Colony colony;



    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        initACO(problem,solutionGenerator);

        while (!terminalCondition.isSatisfied(this,problem))
        {
            colony.makeTour(problem);
            updateBestIfNecessary(colony.getBestSolution(),colony.getBestCost());
            iterationCount++;
        }
    }

    private void initACO(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount =0;
        colony.init(problem,solutionGenerator);
    }

    @Override
    public String defaultName() {
        return null;
    }
}
