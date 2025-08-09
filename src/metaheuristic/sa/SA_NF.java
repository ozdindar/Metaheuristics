package metaheuristic.sa;

import base.NeighboringFunction;
import base.OptimizationProblem;
import exceptions.InvalidStatus;
import metaheuristic.AbstractSMetaheuristic;
import problems.base.InitialSolutionGenerator;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.List;

/**
 * Created by dindar.oz on 01.06.2015.
 */
public class SA_NF extends AbstractSMetaheuristic
{
    List<NeighboringFunction> neighboringFunctions;
    private CoolingSchedule coolingSchedule;

    int iterationCount =0;

    private double minimumTemperature=0;
    private double currentTemperature=100.0;
    private long totalIterationCount= 0;
    private long maxNeighboring;
    private double minCost=0;
    private int maxInnerLoopIteration=5;

    public SA_NF(List<NeighboringFunction> neighboringFunctions, CoolingSchedule cs, double minimumTemperature, long maxNeighboring, double minCost) {
        this.neighboringFunctions = neighboringFunctions;
        this.coolingSchedule = cs;
        this.minimumTemperature = minimumTemperature;
        this.maxNeighboring = maxNeighboring;
        this.minCost = minCost;

    }


    @Override
    public String generateResultString() {
        //todo:
        return null;
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        totalIterationCount=0;

        Individual currentState = currentSolution;

        while ( currentTemperature > minimumTemperature)
        {
            iterationCount=0;
            while(!isInnerLoopFinished(currentState, problem))
            {
                Individual neighbor = generateNeighbor(problem, currentState);

                if ( accepted(currentState.getCost(), neighbor.getCost(), currentTemperature))
                {
                    currentState = neighbor;
                    updateBestIfNecessary(currentState.getRepresentation(),currentState.getCost());
                }
                iterationCount++;
            }
            totalIterationCount += iterationCount;

            fireIterationEvent(new SAIterationEvent(totalIterationCount,getNeighboringCount(),currentTemperature,bestKnownCost,bestKnownSolution,currentState.getCost(),currentState.getRepresentation()));
            //System.out.println(totalIterationCount+"-iteration: Temperature: "+currentTemperature+"  Cost:"+ currentState.getCost());

            if (isFinished(currentState,problem))
                break;


            currentTemperature = updateTemperature(currentTemperature);
        }
        printBest();
    }

    @Override
    public AbstractSMetaheuristic clone() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String defaultName() {
        return "SA_NF";
    }

    private boolean isInnerLoopFinished(Individual currentState, OptimizationProblem problem) {
        if (getNeighboringCount()>maxNeighboring)
            return true;

        if (currentState.getCost()<minCost)
            return true;

        if (iterationCount>maxInnerLoopIteration)
            return true;

        return false;

    }

    private boolean isFinished(Individual currentState, OptimizationProblem problem) {
        if (getNeighboringCount()>maxNeighboring)
            return true;

        if (currentState.getCost()<minCost)
            return true;

        return false;
    }

    private boolean accepted(double oldFitness, double newFitness, double currentTemperature) {

        if (newFitness<oldFitness)
            return true;

        double a = Math.exp((oldFitness-newFitness)/currentTemperature);

        boolean accepted = RandUtil.rollDice(a);
        return accepted;
    }


    public Individual generateNeighbor(OptimizationProblem problem, Individual currentState)
    {
        if (neighboringFunctions.isEmpty())
            throw new InvalidStatus("NeighboringFunction List Empty");


        int mo = RandUtil.randInt(neighboringFunctions.size());

        Individual ni = neighboringFunctions.get(mo).apply(problem,currentState);

        increaseNeighboringCount(); //We got new neighboring

        return ni;

    }

    public double updateTemperature(double t) {
        return coolingSchedule.updateTemperature(t);
    }
}
