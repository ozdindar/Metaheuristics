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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class IslandEA extends AbstractMetaheuristic {

    List<MutationOperator> mutationOperators;
    MutationStrategy mutationStrategy = new SimpleMutationStrategy(0.8,1);

    List<CrossOverOperator> crossOverOperators;
    CrossOverStrategy crossOverStrategy = new SimpleCrossOverStrategy(0.8);

    List<Population> populations = new ArrayList<Population>();
   
    ParentSelector parentSelector;
    VictimSelector victimSelector = new SimpleVictimSelector();

    private int poolSize;
    ForkJoinPool pool ;
    
    int immigrationPeriod=5;
    int immigrantCount = 5;

    private int initialPopulationSize = 50; 

    private int populationCount = 4;
    
    public void setInitialPopulationSize(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    public IslandEA() {

     }

    public IslandEA(int initialPopulationSize) {
        this.initialPopulationSize = initialPopulationSize;
    }

    public IslandEA(List<CrossOverOperator> crossOverOperators, List<MutationOperator> mutationOperators, ParentSelector parentSelector, TerminalCondition terminalCondition) {
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
        return "IslandEA";
    }

    @Override
    public String generateResultString() {
        return bestKnownCost+"";
    }

        public void setImmigrationPeriod(int immigrationPeriod) {
        this.immigrationPeriod = immigrationPeriod;
    }

    public void setImmigrantCount(int immigrantCount) {
        this.immigrantCount = immigrantCount;
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        pool = new ForkJoinPool(poolSize);
        populations.clear();
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount=0;
        this.solutionGenerator = solutionGenerator;
        
        
        for(int i = 0; i < populationCount; i++)
        {
        	Population population = generateInitialPopulation(problem,solutionGenerator,initialPopulationSize);
        	populations.add(i, population);
        }
        
    	while (!terminalCondition.isSatisfied(this,problem))
        {
    		Collection<Callable<Object> > callables= new ArrayList<>();
    		
    		for(int i = 0; i < populationCount; i++)
            {
    			final int current = i;
    			Callable<Object> islandThread = new Callable<Object>() {
    	            @Override
    	            public Object call() throws Exception {
                        Population population = populations.get(current);
    	                for (int iteration = 0; iteration < immigrationPeriod ; iteration++) {

                            generateNextGeneration(problem,population);
                        }
                        return null;
    	            }
    	        };
    			
    	        callables.add(islandThread);
            }
    		pool.invokeAll(callables);
    		//iterationCount+=populationCount;
    		iterationCount+=immigrationPeriod;
            // It s time for migration
    		migrateIslands();


            Individual best = getGlobalBest();
            updateBestIfNecessary(best.getRepresentation(),best.getCost());
            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), best.getCost(),best.getRepresentation()));

        }
    		
    	
                //System.out.println(iterationCount+"-iteration: Average-F:"+ PopulationUtil.averageFitness(population.getIndividuals())+"  Best-F:"+ population.getBest());
        
         printBest();
    }

       
    private Individual getGlobalBest()
    {
    	Individual best = null;
    	for(Population pop:populations)
    	{
    		if(best == null) best = pop.getBest();
    		else if(pop.getBestCost() < best.getCost()) best = pop.getBest();
    	}
    	return best;
    }
    private void migrateIslands() {
    	
    	for(int i = 0; i < populationCount; i++)
    	{
    		Population to = populations.get(i);
    		if(i == 0) 
    			to.sort(new CostBasedComparator());
    		Population from;
    		if(i != populationCount-1) //populations other than the last one
    			from = populations.get(i+1);
    		else 
    			from = populations.get(0);
    		
    		from.sort(new CostBasedComparator());
    		
    		for(int j = 0; j < immigrantCount; j++)
    		{
    			to.remove(to.size()-1-j);//remove worst immigrantCount
    			to.add(from.get(j));//put the neighbor's best
    		}
    	}
    }
    
    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {

        if (params.length<12)
            throw new InvalidParameters("Island-EA needs 12 params. You provided:"+ params.length);

        IslandEA ea = new IslandEA();

        ea.mutationOperators  = EAService.MutationOperators.createMutationOperators(params[0],problem);
        ea.mutationStrategy   = EAService.MutationStrategies.createMutationStrategy(params[1], problem);
        ea.crossOverOperators = EAService.CrossOverOperators.createCrossOverOperators(params[2], problem);
        ea.crossOverStrategy = EAService.CrossOverStrategies.createCrossOverStrategy(params[3],problem);

        ea.victimSelector = EAService.VictimSelectors.createVictimSelector(params[4],problem);
        ea.parentSelector = EAService.ParentSelectors.createParentSelector(params[5],problem);

        ea.terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[6],problem);

        ea.initialPopulationSize = Integer.parseInt(params[7]);

        ea.populationCount = Integer.parseInt(params[8]);

        ea.immigrationPeriod = Integer.parseInt(params[9]);

        ea.immigrantCount = Integer.parseInt(params[10]);

        ea.poolSize = Integer.parseInt(params[11]);


        return ea;
    }

	@Override
	public int getIterationCount() {
		return iterationCount;
	}
}
