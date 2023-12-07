package metaheuristic.es;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAIterationEvent;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.VictimSelector;
import metaheuristic.ea.victimselector.SimpleVictimSelector;
import metaheuristic.tabu.MediumTermMemory;
import problems.base.InitialSolutionGenerator;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 28.05.2015.
 */
public class ES extends AbstractMetaheuristic {

    List<MutationOperator> mutationOperators;

    int mu;
    int lambda;


    public void setDiversityMemory(MediumTermMemory diversityMemory) {
        this.diversityMemory = diversityMemory;
    }

    MediumTermMemory diversityMemory= null;

//
//    ParentSelector parentSelector;
     VictimSelector victimSelector = new SimpleVictimSelector();


    int immigrationPeriod=10;


    public void init(OptimizationProblem problem)
    {
        super.init(problem);
        if (diversityMemory!=null)
            diversityMemory.init(problem,true);
    }




    public ES() {
     }



    public ES(int mu, int lambda, List<MutationOperator> mutationOperators, TerminalCondition terminalCondition) {
        this.mu = mu;
        this.lambda = lambda;
        this.mutationOperators = mutationOperators;

        this.terminalCondition = terminalCondition;
    }


    public Population generateInitialPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, int c) {
        List<Representation> initialStates = solutionGenerator.generate(problem,c);
        Population initialPopulation = new ListPopulation();
        for(Representation r: initialStates)
        {
            Individual i = new SimpleIndividual(r,problem.cost(r));
            if (diversityMemory!=null)
                diversityMemory.update(problem,i,null);
            initialPopulation.add(i);
            updateBestIfNecessary(i.getRepresentation(),i.getCost());
            increaseNeighboringCount();
        }
        return initialPopulation;
    }

    public void removeVictims(OptimizationProblem problem, List<Individual> population, int victimCount )
    {
        List<Individual> victims = victimSelector.selectVictims(problem,population,victimCount);

        for(Individual victim:victims)
            population.remove(victim);
    }

    public Population generateNextGeneration(OptimizationProblem problem, Population oldGen) {
        List<Individual> newPopulation = new ArrayList<>(lambda);
        newPopulation.addAll(oldGen.getIndividuals());
        for (int o = 0; o < lambda; o++) {
            MutationOperator mutationOperator = pickMutation();
            Individual parent = pickParent(problem,oldGen);
            Representation offRep = mutationOperator.apply(problem,parent.getRepresentation());
            Individual offspring = new SimpleIndividual(offRep,problem.cost(offRep));
            newPopulation.add(offspring);
            if (diversityMemory!=null)
                diversityMemory.update(problem,offspring,null);
            increaseNeighboringCount();
        }

        removeVictims(problem,newPopulation,mu);

        return new ListPopulation(newPopulation);
    }

    private Individual pickParent(OptimizationProblem problem, Population oldGen) {
        int parentIndex= RandUtil.randInt(oldGen.size());
        return oldGen.get(parentIndex);
    }

    private MutationOperator pickMutation() {
        int mutationIndex= RandUtil.randInt(mutationOperators.size());
        return mutationOperators.get(mutationIndex);
    }


    int iterationCount =0;


    @Override
    public String defaultName() {
        return "ES";
    }

    @Override
    public String generateResultString() {
        return bestKnownCost+"";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }




    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount=0;
        this.solutionGenerator = solutionGenerator;
        Population population = generateInitialPopulation(problem,solutionGenerator,mu);

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

        printBest();

    }

    private void acceptImmigrants(OptimizationProblem problem, Population population) {

        int immigrantCount = mu/2;
        removeVictims(problem,population.getIndividuals(),immigrantCount);
        Population immigrants= generateInitialPopulation(problem,solutionGenerator,immigrantCount);
        population.add(immigrants);
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {

        if (params.length<8)
            throw new InvalidParameters("EA needs 8 params. You provided:"+ params.length);

        ES es = new ES();

        es.mutationOperators  = EAService.MutationOperators.createMutationOperators(params[0],problem);


        es.victimSelector = EAService.VictimSelectors.createVictimSelector(params[4],problem);


        es.terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[6],problem);

        es.mu = Integer.parseInt(params[7]);

        return es;
    }
}
