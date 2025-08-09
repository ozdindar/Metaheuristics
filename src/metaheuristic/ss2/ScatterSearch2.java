package metaheuristic.ss2;


import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.BaseSIterationEvent;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.terminalcondition.IterationBasedTC;
import metaheuristic.ea.terminalcondition.OrCompoundTC;
import metaheuristic.tabu.SolutionMemoryTabuList;
import metaheuristic.tabu.TabuSearch;
import metaheuristic.tabu.mediumtermmemory.MOTAPFrequencyMatrix;
import metaheuristic.tabu.mediumtermmemory.MOTAPRecencyMatrix;
import problems.base.InitialSolutionGenerator;
import problems.motap.crossover.SimpleMOTACrossOver;
import problems.motap.mutation.GRMR_Tabu;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 16.12.2016.
 */
public class ScatterSearch2 extends AbstractMetaheuristic {

    //Population population;
    Population referenceSet = new ListPopulation();

    private int iterationCount=0;

    private int refSetSize = 10;
    private int elitesCount= 6;

    AbstractSMetaheuristic localSearch;
    CrossOverOperator combinationOperator;


    int populationSize = 50;



    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    public ScatterSearch2(AbstractSMetaheuristic localSearch, CrossOverOperator combinationOperator, TerminalCondition tc) {
        this.localSearch = localSearch;
        this.combinationOperator = combinationOperator;
        this.terminalCondition = tc;
    }

    public Population generateInitialPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, int c) {

        Population population = new ListPopulation();

        while (population.size()<populationSize)
        {
            List<Representation> initialStates = solutionGenerator.generate(problem,populationSize-population.size());
            for (Representation r: initialStates)
            {
                SimpleIndividual i = new SimpleIndividual(r,problem.cost(r));
                localSearch.setCurrentSolution(i);
                localSearch.perform(problem,solutionGenerator);
                increaseNeighboringCount();
                i = new SimpleIndividual(localSearch.getBestKnownSolution(),localSearch.getBestKnownCost());
                if (!population.contains(i))
                {
                    population.add(i);
                    updateBestIfNecessary(i.getRepresentation(),i.getCost());
                }

            }
        }


        return population;
    }


    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount =0;

        Population population = generateInitialPopulation(problem,solutionGenerator,populationSize);

        while (!terminalCondition.isSatisfied(this,problem))
        {
            updateReferenceSet(problem,solutionGenerator,population);

            population = generateNewPopulation(problem,solutionGenerator);

            improvePopulation(problem,solutionGenerator,population);
            iterationCount++;
            fireIterationEvent(new BaseSIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution,population.getBestCost(),population.getBest().getRepresentation()));
        }

        printBest();

    }

    private Population generateNewPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Population population = new ListPopulation();
        for (int i1=0; i1<refSetSize-1;i1++)
        {
            for (int i2=i1+1; i2<refSetSize;i2++)
            {
                Individual ind1 = referenceSet.get(i1);
                Individual ind2 = referenceSet.get(i2);
                List<Representation> offSprings =combinationOperator.apply(problem,ind1.getRepresentation(),ind2.getRepresentation());
                for (Representation r:offSprings)
                {
                    population.add(new SimpleIndividual(r,problem.cost(r)));
                }
            }
        }
        updateBestIfNecessary(population.getBest().getRepresentation(),population.getBestCost());
        return population;
    }


    private void updateReferenceSet(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, Population population) {
        referenceSet.clear();
        for ( ; referenceSet.size()<elitesCount&& !population.isEmpty(); )
        {
            Individual best = population.getBest();
            population.remove(best);
            if (!referenceSet.contains(best))
                referenceSet.add(best);
        }

        while (referenceSet.size()<refSetSize)
        {
            Representation rep = solutionGenerator.generate(problem,1).get(0);
            Individual i = new SimpleIndividual(rep,problem.cost(rep));
            if (!referenceSet.contains(i))
                referenceSet.add(i);
        }
    }



    private void improvePopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, Population population) {
        Population improved = new ListPopulation();
        for (Individual i:population.getIndividuals())
        {
            localSearch.setCurrentSolution(i);
            localSearch.perform(problem,solutionGenerator);
            improved.add(new SimpleIndividual(localSearch.getBestKnownSolution(),localSearch.getBestKnownCost()));
            increaseNeighboringCount();
        }
        population = improved;

        updateBestIfNecessary(population.getBest().getRepresentation(),population.getBestCost());
    }

    @Override
    public String defaultName() {
        return "SS-2";
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<6)
            throw new InvalidParameters("SS needs 6 params. You provided:"+ params.length);

        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[0],problem);

        int localSearchIterationCount = Integer.parseInt(params[1]);


        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        //muList.add(new GRMR_Tabu(3,5));
        muList.add(new GRMR_Tabu());
        TerminalCondition tcIteration = new IterationBasedTC(localSearchIterationCount);
        TerminalCondition tc= new OrCompoundTC(Arrays.asList(tcIteration));

        TabuSearch ts = new TabuSearch(tc,muList,new SolutionMemoryTabuList(10),null,5);
        ts.setIntensityMemory(new MOTAPRecencyMatrix(Integer.parseInt(params[3])));
        ts.setDiversityMemory(new MOTAPFrequencyMatrix(Integer.parseInt(params[4])));
        ts.setNeighborhoodSize(Integer.parseInt(params[5]));
        ts.setName("TABU2");
        ts.setClearMemoryAtInit(false);

        //SA sa = new SA(muList,new LineerCooling(0.1),0,300,Double.MIN_VALUE);
        ScatterSearch2 ss = new ScatterSearch2(ts,new SimpleMOTACrossOver(),terminalCondition);
        ss.refSetSize = Integer.parseInt(params[2]);

        return ss;
    }
}
