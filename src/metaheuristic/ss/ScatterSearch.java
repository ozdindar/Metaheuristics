package metaheuristic.ss;


import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.island.IslandModul;
import metaheuristic.ls.LocalSearch;
import metaheuristic.tabu.TabuIterationEvent;
import problems.base.InitialSolutionGenerator;
import problems.motap.crossover.SimpleMOTACrossOver;
import problems.motap.mutation.GRMR.GRMRNF;
import representation.CostBasedComparator;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 16.12.2016.
 */
public class ScatterSearch extends AbstractMetaheuristic implements IslandModul {

    Population population;
    Population referenceSet = new ListPopulation();

    private int iterationCount=0;

    private int refSetSize = 5;
    private int elitesCount= refSetSize/2;

    AbstractSMetaheuristic localSearch;
    CrossOverOperator combinationOperator;


    public void setInitialPopulationSize(int initialPopulationSize) {
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    public ScatterSearch(AbstractSMetaheuristic localSearch, CrossOverOperator combinationOperator, TerminalCondition tc) {
        this.localSearch = localSearch;
        this.combinationOperator = combinationOperator;
        this.terminalCondition = tc;
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

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        population = null;
        iterationCount =0;
    }

    @Override
    public void runFor(int migrationPeriod, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        if (population==null)
        {
            population = generateInitialPopulation(problem,solutionGenerator,refSetSize*10);
            improvePopulation(problem, solutionGenerator);

        }


        for (int i = 0; i < migrationPeriod; i++) {

            updateReferenceSet(problem,solutionGenerator);

            generateNewPopulation(problem,solutionGenerator);

            improvePopulation(problem,solutionGenerator);
            iterationCount++;
            //fireIterationEvent(new TabuIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
        }

    }

    @Override
    public Individual getBestSolution() {
        return new SimpleIndividual(bestKnownSolution,bestKnownCost);
    }

    @Override
    public ArrayList<Individual> getImmigrants(int immigrantCount) {
        population.sort(new CostBasedComparator());
        ArrayList<Individual> immigrants  = new ArrayList<>(immigrantCount);
        for (int i = 0; i < immigrantCount; i++) {
            immigrants.add(population.get(i));
        }

        return immigrants;
    }

    @Override
    public void acceptImmigrants(ArrayList<Individual> immigrants) {
        updatePopulation(immigrants);
    }

    private void updatePopulation(ArrayList<Individual> immigrants) {
        population.sort(new CostBasedComparator());


        int toBeRemoved = immigrants.size();
        for (int i=0 ;i<toBeRemoved;i++)
            population.remove(population.size()-1);

        for (int i=0 ;i<toBeRemoved;i++)
            population.add(immigrants.get(i));



    }


    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount =0;

        population = generateInitialPopulation(problem,solutionGenerator,refSetSize*10);

        improvePopulation(problem, solutionGenerator);

        while (!terminalCondition.isSatisfied(this,problem))
        {
            updateReferenceSet(problem,solutionGenerator);

            generateNewPopulation(problem,solutionGenerator);

            improvePopulation(problem,solutionGenerator);
            iterationCount++;
            fireIterationEvent(new TabuIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
        }

        printBest();

    }

    private void generateNewPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        population.clear();
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
    }


    private void updateReferenceSet(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        referenceSet.clear();
        for (int i=0; i<elitesCount; i++)
        {
            Individual best = population.getBest();
            population.remove(best);
            referenceSet.add(best);
        }

        while (referenceSet.size()<refSetSize)
        {
            Representation rep = solutionGenerator.generate(problem,1).get(0);
            Individual i = new SimpleIndividual(rep,problem.cost(rep));
            referenceSet.add(i);
        }
    }



    private void improvePopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Population improved = new ListPopulation();
        for (Individual i:population.getIndividuals())
        {
            localSearch.setCurrentSolution(i);
            localSearch.perform(problem,solutionGenerator);
            improved.add(new SimpleIndividual(localSearch.getBestKnownSolution(),localSearch.getBestKnownCost()));
            increaseNeighboringCount((int) localSearch.getNeighboringCount());
        }
        population = improved;
        updateBestIfNecessary(population.getBest().getRepresentation(),population.getBestCost());
    }

    @Override
    public String defaultName() {
        return "SS";
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<2)
            throw new InvalidParameters("SS needs 2 params. You provided:"+ params.length);

        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[0],problem);




        List<NeighboringFunction> muList = new ArrayList<NeighboringFunction>();
        muList.add(new GRMRNF(3));



        AbstractSMetaheuristic ls = new LocalSearch(muList);


        //SA sa = new SA(muList,new LineerCooling(0.1),0,300,Double.MIN_VALUE);
        ScatterSearch ss = new ScatterSearch(ls,new SimpleMOTACrossOver(),terminalCondition);
        ss.refSetSize = Integer.parseInt(params[1]);

        return ss;
    }
}
