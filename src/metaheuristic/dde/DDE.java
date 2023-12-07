package metaheuristic.dde;

/**
 * Created by dindar.oz on 26.06.2015.
 */

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.crossover.PTLCutCrossover;
import metaheuristic.ea.mutation.InsertMutation;
import problems.base.InitialSolutionGenerator;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.List;

/**
 * this Discrete-DE is written according to
 * See: Tasgetiren, Pan, Suganthan, Liang, A Discrete DE algorithm for the
 * No-wait Flowshop Scheduling Problem with Total Flowtime Criterion,p.254 2007, (conf)
 * See: Pan, tasgetiren, Liang, A Discrete DE algorithm for the permutation
 * flowshop scheduling problem, 2008, (journal)
 * @author falkaya
 *
 */
public class DDE extends AbstractMetaheuristic {
    private int iterationCount=0;
    private int initialPopulationSize;
    private double mutationProbability;

    private MutationOperator mutation;
    private CrossOverOperator crossOver;
    private double crossOverProbability;
    private TerminalCondition terminalCondition;

    public DDE(int initialPopulationSize, double mutationProbability, double crossOverProbability, TerminalCondition terminalCondition, int mutationRepetition) {
        this.initialPopulationSize = initialPopulationSize;
        this.mutationProbability = mutationProbability;
        this.crossOverProbability = crossOverProbability;
        this.terminalCondition = terminalCondition;

        this.mutation = new InsertMutation(mutationRepetition);
        this.crossOver = new PTLCutCrossover();
    }


    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    public Population generateInitialPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, int c) {
        List<Representation> initialStates = solutionGenerator.generate(problem,c);
        Population initialPopulation = new ListPopulation();
        for(Representation r: initialStates)
        {
            Individual i = new SimpleIndividual(r,problem.cost(r));
            initialPopulation.add(i);
            updateBestIfNecessary(i.getRepresentation(), i.getCost());
            increaseNeighboringCount();
        }
        return initialPopulation;
    }

    @Override
    public void perform(OptimizationProblem problem,InitialSolutionGenerator solutionGenerator) {
        Population population = generateInitialPopulation(problem,solutionGenerator,initialPopulationSize);

        while (!terminalCondition.isSatisfied(this,population,problem)) {
            population = generateNextGeneration(problem, population);
            iterationCount++;
            fireIterationEvent(new DDEIterationEvent(iterationCount,getNeighboringCount(), bestKnownCost,bestKnownSolution));
        }

        printBest();
    }

    @Override
    public String defaultName() {
        return "DDE";
    }


    private Population generateNextGeneration(OptimizationProblem problem, Population population) {
        Population newPopulation = new ListPopulation();

        for (Individual cs: population.getIndividuals())
        {
            Individual v = (RandUtil.rollDice(mutationProbability)) ? mutateBest(problem):cs;
            v = (RandUtil.rollDice(crossOverProbability)) ? applyCrossOver(problem, cs, v):v  ;
            v = (cs.getCost()<v.getCost()) ? cs:v;

            newPopulation.add(v);
        }
        return newPopulation ;
    }

    private Individual applyCrossOver(OptimizationProblem problem, Individual cs, Individual v) {

        Representation offspringR = crossOver.apply(problem,cs.getRepresentation(),v.getRepresentation()).get(0);
        double offspringCost= problem.cost(offspringR);
        Individual offspring = new SimpleIndividual(offspringR,offspringCost);
        updateBestIfNecessary(offspringR,offspringCost);
        increaseNeighboringCount();
        return offspring;
    }

    private Individual mutateBest(OptimizationProblem problem) {
        Representation mutant = mutation.apply(problem, bestKnownSolution);
        double mutantCost = problem.cost(mutant);
        updateBestIfNecessary(mutant,mutantCost);
        increaseNeighboringCount();
        return new SimpleIndividual(mutant,mutantCost);
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<5)
            throw new InvalidParameters("D-DE needs 5 params. You provided:"+ params.length);

        int initialPopulationSize = Integer.parseInt(params[0]);
        double mutationProbability = Double.parseDouble(params[1]);
        double crossOverProbability = Double.parseDouble(params[2]);
        int mutationRepetition = Integer.parseInt(params[3]);
        TerminalCondition tc = EAService.TerminalConditions.createTerminalCondition(params[4],problem);

        return new DDE(initialPopulationSize,mutationProbability,crossOverProbability,tc,mutationRepetition);
    }
}
