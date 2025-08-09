package metaheuristic.ss;


import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.BaseSIterationEvent;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.tabu.MediumTermMemory;
import metaheuristic.tabu.TabuService;
import metaheuristic.tabu.mediumtermmemory.MOTAPFrequencyMatrix;
import metaheuristic.tabu.mediumtermmemory.MOTAPRecencyMatrix;
import problems.base.InitialSolutionGenerator;
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
 * Created by dindar.oz on 16.12.2016.
 */
public class PSSWM extends AbstractMetaheuristic {

    MediumTermMemory frequencyMatrix;
    MediumTermMemory recencyMatrix;
    Population population;
    Population referenceSet = new ListPopulation();



    private int iterationCount=0;

    private int refSetSize = 20;
    private int diversifiedCount = 6;
    private int elitesCount= (int) (.50*refSetSize);
    private int cloneNumber;
    ForkJoinPool pool;

    AbstractSMetaheuristic[] LSList;
    CrossOverOperator combinationOperator;

    public PSSWM() {
    }


    public void setInitialPopulationSize(int initialPopulationSize) {
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    public PSSWM(AbstractSMetaheuristic localSearch, CrossOverOperator combinationOperator, TerminalCondition tc, int cloneNumber) {
        this.cloneNumber = cloneNumber;
        LSList = new AbstractSMetaheuristic[cloneNumber];
        for(int i = 0 ; i<cloneNumber ; i++){
            LSList[i]=localSearch.clone();
        }
        this.combinationOperator = combinationOperator;
        this.terminalCondition = tc;
        pool = new ForkJoinPool(cloneNumber);

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
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount =0;

        if (frequencyMatrix !=null)
            frequencyMatrix.init(problem,true);
        if (recencyMatrix !=null)
            recencyMatrix.init(problem,true);
        population = generateInitialPopulation(problem,solutionGenerator,refSetSize*(refSetSize-1)/2);

        improvePopulation(problem, solutionGenerator);

        while (!terminalCondition.isSatisfied(this,problem))
        {
            updateReferenceSet(problem,solutionGenerator);

            generateNewPopulation(problem,solutionGenerator);

            improvePopulation(problem,solutionGenerator);
            iterationCount++;
            fireIterationEvent(new BaseSIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution,population.getBestCost(),population.getBest().getRepresentation()));
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
                    Individual i = new SimpleIndividual(r,problem.cost(r));
                    population.add(i);
                    if (frequencyMatrix != null)
                        frequencyMatrix.update(problem,i,null);
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


        while (referenceSet.size()<refSetSize- diversifiedCount)
        {
            Individual i = null;
            if (recencyMatrix !=null) {
                Representation rep = recencyMatrix.generate(problem);    // Generate kullanıyordu diversify a çevirdim.
                i = new SimpleIndividual(rep,problem.cost(rep));
            }
            else {
                Representation rep = solutionGenerator.generate(problem, 1).get(0);
                i = new SimpleIndividual(rep, problem.cost(rep));
            }
            referenceSet.add(i);
        }


        while (referenceSet.size()<refSetSize)
        {
            Individual i = null;
            if (frequencyMatrix !=null) {
                Representation rep = frequencyMatrix.generate(problem);    // Generate kullanıyordu diversify a çevirdim.
                i = new SimpleIndividual(rep,problem.cost(rep));
                frequencyMatrix.update(problem,i,null);
            }
            else {
                Representation rep = solutionGenerator.generate(problem, 1).get(0);
                i = new SimpleIndividual(rep, problem.cost(rep));
            }
            referenceSet.add(i);
        }
    }



    private void improvePopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Population improved = new ListPopulation();

        Collection<Callable<Object>> callables= new ArrayList<>();

        int individualIndex=0;
        while (individualIndex<population.size())
        {
            callables.clear();

            for (int ls = 0; ls<LSList.length && individualIndex<population.size(); ls++) {
                AbstractSMetaheuristic localSearch = LSList[ls];
                Individual i = population.get(individualIndex);
                Callable<Object> improveThread = new Callable<Object>() {
                    @Override
                    public Object call() {
                        localSearch.setCurrentSolution(i);
                        localSearch.perform(problem, solutionGenerator);
                        improved.add(new SimpleIndividual(localSearch.getBestKnownSolution(), localSearch.getBestKnownCost()));
                        updateBestIfNecessary(localSearch.getBestKnownSolution(), localSearch.getBestKnownCost());
                        increaseNeighboringCount((int) localSearch.getNeighboringCount());
                        return null;
                    }
                };
                callables.add(improveThread);
                individualIndex++;
            }

            pool.invokeAll(callables);

            fireIterationEvent(new BaseSIterationEvent(iterationCount, getNeighboringCount(), bestKnownCost, bestKnownSolution,population.getBestCost(),population.getBest().getRepresentation()));
            for(int i = 0 ; i<cloneNumber ; i++){
                if (recencyMatrix!=null)
                    recencyMatrix.update(problem,new SimpleIndividual(LSList[i].getBestKnownSolution(),LSList[i].getBestKnownCost()),null);
            }
        }


        population = improved;
        //updateBestIfNecessary(population.getBest().getRepresentation(),population.getBestCost());
    }

    @Override
    public String defaultName() {
        return "SS";
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {

        PSSWM ss = new PSSWM();

        AbstractSMetaheuristic localSearch = SSService.SMetaheuristics.createAbstractSMetaheuristic(params[0],problem);

        ss.cloneNumber = Integer.parseInt(params[1]);

        ss.pool = new ForkJoinPool(ss.cloneNumber);

        ss.LSList = new AbstractSMetaheuristic[ss.cloneNumber];
        for(int i = 0 ; i<ss.cloneNumber ; i++){
            ss.LSList[i]=localSearch.clone();
        }

        ss.refSetSize = Integer.parseInt(params[2]);

        ss.frequencyMatrix = TabuService.MediumTermMemories.createMediumTermMemory(params[3],problem);

        ss.recencyMatrix = TabuService.MediumTermMemories.createMediumTermMemory(params[4],problem);

        ss.terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[5],problem);

        ss.combinationOperator = EAService.CrossOverOperators.createCrossOverOperator(params[6],problem);



        return ss;
    }

    public void setMemory(MOTAPFrequencyMatrix memory) {
        frequencyMatrix = memory;
    }
    public void setMemory(MOTAPRecencyMatrix memory) {
        recencyMatrix = memory;
    }
}
