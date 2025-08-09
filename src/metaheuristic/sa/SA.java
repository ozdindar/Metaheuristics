package metaheuristic.sa;

import base.OptimizationProblem;
import exceptions.InvalidStatus;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.ea.base.MutationOperator;
import problems.base.InitialSolutionGenerator;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.List;

/**
 * Created by dindar.oz on 01.06.2015.
 */
public class SA extends AbstractSMetaheuristic
{
    private static final double INITIAL_TEMPERATURE = 100;
    List<MutationOperator> neighboringFunctions;
    private CoolingSchedule coolingSchedule;

    int iterationCount =0;

    private double minimumTemperature=0;
    private double currentTemperature=INITIAL_TEMPERATURE;
    private long totalIterationCount= 0;
    private long maxNeighboring;
    private double minCost=0;
    private int maxInnerLoopIteration=5;

    public SA(List<MutationOperator> neighboringFunctions, CoolingSchedule cs, double minimumTemperature, long maxNeighboring, double minCost) {
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
        return (int) totalIterationCount;
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
                fireIterationEvent(new SAIterationEvent(totalIterationCount,
                        getNeighboringCount(),
                        currentTemperature,
                        bestKnownCost,
                        bestKnownSolution,
                        currentState.getCost(),
                        currentState.getRepresentation()));
                iterationCount++;
            }
            totalIterationCount += iterationCount;

            if (isFinished(currentState,problem))
                break;


            currentTemperature = updateTemperature(currentTemperature);
        }
        //printBest();
    }

    @Override
    public AbstractSMetaheuristic clone() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String defaultName() {
        return "SA";
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

        Representation nr = neighboringFunctions.get(mo).apply(problem,currentState.getRepresentation());
        Individual ni = new SimpleIndividual(nr,problem.cost(nr)) ;

        increaseNeighboringCount(); //We got new neighboring

        return ni;

    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        if (terminalCondition != null)
            terminalCondition.init();
        coolingSchedule.init();
        currentTemperature = INITIAL_TEMPERATURE;
    }

    public double updateTemperature(double t) {
        return coolingSchedule.updateTemperature(t);
    }
}
