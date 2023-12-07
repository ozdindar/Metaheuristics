package metaheuristic.ea;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.MutationStrategy;
import metaheuristic.ea.base.VictimSelector;
import metaheuristic.ea.mutation.SimpleMutationStrategy;
import metaheuristic.ea.victimselector.SimpleVictimSelector;
import problems.base.InitialSolutionGenerator;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;

import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class SimpleMutationSearch extends AbstractMetaheuristic {

    List<MutationOperator> mutationOperators;
    MutationStrategy mutationStrategy = new SimpleMutationStrategy(0.4,1);


    TerminalCondition terminalCondition;

    private int initialPopulationSize ;
    private VictimSelector victimSelector = new SimpleVictimSelector();

    public void setInitialPopulationSize(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    public SimpleMutationSearch() {
        this.initialPopulationSize = 20;
    }

    public SimpleMutationSearch(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    public SimpleMutationSearch( List<MutationOperator> mutationOperators, TerminalCondition terminalCondition) {
        this.initialPopulationSize = 20;
        this.mutationOperators = mutationOperators;
        this.terminalCondition = terminalCondition;
    }


    public Population generateInitialPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, int c) {
        List<Representation> initialStates = solutionGenerator.generate(problem,c);
        Population initialPopulation = new ListPopulation();
        for(Representation r: initialStates)
        {
            Individual i = new SimpleIndividual(r,problem.cost(r));
            initialPopulation.add(i);
            updateBestIfNecessary(i.getRepresentation(),i.getCost());
            increaseNeighboringCount();
        }
        return initialPopulation;
    }



    public Population generateNextGeneration(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator,Population oldGen) {
        acceptImmigrants(problem,solutionGenerator,oldGen);
        mutationStrategy.applyMutations(problem, oldGen,mutationOperators);
        increaseNeighboringCount(mutationStrategy.getMutationCount());
        return oldGen;
    }

    int iterationCount =0;


    @Override
    public String defaultName() {
        return "SMS";
    }

    @Override
    public String generateResultString() {
        return bestKnownCost+"";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }


    private void acceptImmigrants(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator,Population oldGen) {

        Population immigrants = generateInitialPopulation(problem,solutionGenerator,2);

        List<Individual> victims = victimSelector.selectVictims(problem, oldGen.getIndividuals(), immigrants.size());

        oldGen.removeAll(victims);
        oldGen.add(immigrants);
        increaseNeighboringCount(immigrants.size());

    }


    @Override
    public void perform( OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Population population = generateInitialPopulation(problem,solutionGenerator,initialPopulationSize);

        while (!terminalCondition.isSatisfied(this,population,problem))
        {
            Population nextGen = generateNextGeneration(problem,solutionGenerator,population);

            population = nextGen;
            iterationCount++;


            Individual best = population.getBest();
            updateBestIfNecessary(best.getRepresentation(),best.getCost());
            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), best.getCost(),best.getRepresentation()));

        }

        printBest();

    }



    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {

        if (params.length<8)
            throw new InvalidParameters("EA needs 8 params. You provided:"+ params.length);

        SimpleMutationSearch sms = new SimpleMutationSearch();

        sms.mutationOperators  = EAService.MutationOperators.createMutationOperators(params[0],problem);
        sms.mutationStrategy   = EAService.MutationStrategies.createMutationStrategy(params[1], problem);

        sms.terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[2],problem);

        sms.initialPopulationSize = Integer.parseInt(params[3]);

        return sms;
    }
}
