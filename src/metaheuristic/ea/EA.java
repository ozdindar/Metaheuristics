package metaheuristic.ea;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.base.*;
import metaheuristic.ea.crossover.SimpleCrossOverStrategy;
import metaheuristic.ea.mutation.SimpleMutationStrategy;
import metaheuristic.ea.victimselector.SimpleVictimSelector;
import problems.base.InitialSolutionGenerator;
import representation.CostBasedComparator;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;

import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class EA extends AbstractMetaheuristic {

    List<MutationOperator> mutationOperators;
    MutationStrategy mutationStrategy = new SimpleMutationStrategy(0.8,1);

    List<CrossOverOperator> crossOverOperators;
    CrossOverStrategy crossOverStrategy = new SimpleCrossOverStrategy(0.8);

    ParentSelector parentSelector;
    VictimSelector victimSelector = new SimpleVictimSelector();


    int immigrationPeriod=100;
    int immigrantCount = 20;

    private int initialPopulationSize = 50; ;

    public void setInitialPopulationSize(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    public EA() {
     }

    public EA(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    public EA(List<CrossOverOperator> crossOverOperators, List<MutationOperator> mutationOperators, ParentSelector parentSelector, TerminalCondition terminalCondition) {
        this.crossOverOperators = crossOverOperators;
        this.mutationOperators = mutationOperators;
        this.parentSelector = parentSelector;
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



    public Population generateNextGeneration(OptimizationProblem problem, Population oldGen) {
        applyCrossOver(problem, oldGen);
        mutationStrategy.applyMutations(problem, oldGen,mutationOperators);
        increaseNeighboringCount( mutationStrategy.getMutationCount());
        return oldGen;
    }

    private void applyCrossOver(OptimizationProblem problem, Population oldGen) {
        if (!crossOverOperators.isEmpty())
        {
            List<Individual> parents = parentSelector.selectParents(problem, oldGen.getIndividuals());
            List<Individual> offsprings = crossOverStrategy.generateOffsprings(problem, parents, crossOverOperators);
            List<Individual> victims = victimSelector.selectVictims(problem, oldGen.getIndividuals(), offsprings.size());

            for (Individual offspring:offsprings)
            {
                if (!oldGen.contains(offspring))
                {
                    oldGen.remove(victims.get(0));
                    oldGen.add(offspring);
                    victims.remove(0);
                }
            }

            increaseNeighboringCount(offsprings.size()) ;
        }
    }

    int iterationCount =0;


    @Override
    public String defaultName() {
        return "EA";
    }

    @Override
    public String generateResultString() {
        return bestKnownCost+"";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    public void setImmigrationPeriod(int immigrationPeriod) {
        this.immigrationPeriod = immigrationPeriod;
    }

    public void setImmigrantCount(int immigrantCount) {
        this.immigrantCount = immigrantCount;
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount=0;
        this.solutionGenerator = solutionGenerator;
        Population population = generateInitialPopulation(problem,solutionGenerator,initialPopulationSize);

        while (!terminalCondition.isSatisfied(this,population,problem))
        {
            Population nextGen = generateNextGeneration(problem,population);

            population = nextGen;
            iterationCount++;

            if (immigrationPeriod>0 && (iterationCount %immigrationPeriod ==0) )
            {
                acceptImmigrants(problem,population);
            }

            Individual best = population.getBest();
            updateBestIfNecessary(best.getRepresentation(),best.getCost());
            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), best.getCost(),best.getRepresentation()));
            //System.out.println(iterationCount+"-iteration: Average-F:"+ PopulationUtil.averageFitness(population.getIndividuals())+"  Best-F:"+ population.getBest());
        }

       // printBest();

    }

    private void acceptImmigrants(OptimizationProblem problem, Population population) {
        Population immigrants = generateInitialPopulation(problem, solutionGenerator, immigrantCount);
        population.sort(new CostBasedComparator());
        population = population.subPopulation(0,population.size()-immigrantCount);
        population.add(immigrants);
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {

        if (params.length<8)
            throw new InvalidParameters("EA needs 8 params. You provided:"+ params.length);

        EA ea = new EA();

        ea.mutationOperators  = EAService.MutationOperators.createMutationOperators(params[0],problem);
        ea.mutationStrategy   = EAService.MutationStrategies.createMutationStrategy(params[1], problem);
        ea.crossOverOperators = EAService.CrossOverOperators.createCrossOverOperators(params[2], problem);
        ea.crossOverStrategy = EAService.CrossOverStrategies.createCrossOverStrategy(params[3],problem);

        ea.victimSelector = EAService.VictimSelectors.createVictimSelector(params[4],problem);
        ea.parentSelector = EAService.ParentSelectors.createParentSelector(params[5],problem);

        ea.terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[6],problem);

        ea.initialPopulationSize = Integer.parseInt(params[7]);

        return ea;
    }
}
