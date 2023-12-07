package metaheuristic.sos;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.*;
import metaheuristic.ea.crossover.SimpleCrossOverStrategy;
import metaheuristic.ea.mutation.SimpleMutationStrategy;
import metaheuristic.ea.victimselector.SimpleVictimSelector;
import problems.base.InitialSolutionGenerator;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;
import util.PopulationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 24.06.2015.
 */
public class SOS extends AbstractMetaheuristic {

    int iterationCount =0;

    List<MutationOperator> mutationOperators;
    MutationStrategy mutationStrategy = new SimpleMutationStrategy(0.4,1);

    List<CrossOverOperator> crossOverOperators;
    CrossOverStrategy crossOverStrategy = new SimpleCrossOverStrategy(0.4);

    ParentSelector parentSelector;
    VictimSelector victimSelector = new SimpleVictimSelector();

    Population parentPopulation = new ListPopulation();
    List< Population > childPopulations = new ArrayList<>();
    private int initialPopulationSize=100;
    private TerminalCondition terminalCondition;
    private int forkInterval =10;
    private double childDiameter = 0.4;
    private int minChildPopulation =10;
    private int maxChildPopulation = 20;
    private int maxChildren = 5;
    private int minParentPopulation = 20;
    private int totalPopulationCount= 30;

    public SOS(List<CrossOverOperator> coList, List<MutationOperator> muList, ParentSelector ps, TerminalCondition tc) {
        mutationOperators = muList;
        crossOverOperators = coList;
        parentSelector = ps;
        terminalCondition = tc;
    }

    @Override
    public String generateResultString() {
        return null;
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    public List<Individual> generateInitialPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, int c) {
        List<Representation> initialStates = solutionGenerator.generate(problem,c);
        List<Individual> initialPopulation = new ArrayList<>();
        for(Representation r: initialStates)
        {
            Individual i = new SimpleIndividual(r,problem.cost(r));
            initialPopulation.add(i);
            updateBestIfNecessary(i.getRepresentation(),i.getCost());
        }
        increaseNeighboringCount(c);
        return initialPopulation;
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator)
    {
        childDiameter = childDiameter*problem.maxDistance();
        parentPopulation.add(generateInitialPopulation(problem, solutionGenerator,initialPopulationSize));

        while (!terminalCondition.isSatisfied(this,problem))
        {
            iterateOneCycle(problem);
        }
        printBest();
    }

    public String defaultName() {
        return "SOS";
    }

    private void iterateOneCycle(OptimizationProblem problem) {
        performECIterations(problem);

        if ((iterationCount%forkInterval)==0)
        {
            forkIfPossible(problem);
        }

        adjustPopulations(problem);
        iterationCount++;

        fireIterationEvent(new SOSIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution,childPopulations.size()));
    }

    private void adjustPopulations(OptimizationProblem problem) {
        double threshold = PopulationUtil.averageFitness(parentPopulation.getIndividuals());

        List<Population> newChildPopulations = new ArrayList<>();
        for (int i = 0; i < childPopulations.size(); i++) {
            if ((newChildPopulations.size() > maxChildren) || (childPopulations.get(i).getBestCost() > threshold))
                continue;

            newChildPopulations.add(childPopulations.get(i));
        }

        childPopulations = newChildPopulations;


        removeTooCloseChildren();


        for (int i = 0; i < childPopulations.size(); i++) {
            adjustPopulation(i);
        }

        removeEmptyChildren();


        removeTooCloseParentIndividuals();

        int diff = 0;
        if (parentPopulation.size() < minParentPopulation) {
            diff = minParentPopulation - parentPopulation.size();
        }

        int populationCount = getPopulationCount();
        if (diff < totalPopulationCount-populationCount)
        {
            diff = totalPopulationCount-populationCount;
        }

        if (diff>0)
        {
            parentPopulation.add(generateInitialPopulation(problem, solutionGenerator,diff));
        }

    }

    private void removeTooCloseParentIndividuals() {
        Population newParentPopulation = new ListPopulation();

        for (int i=0;i<parentPopulation.size();i++)
        {
            Individual pi = parentPopulation.get(i);
            boolean toBeAdded = true;

            for (int c=0;c<childPopulations.size();c++)
            {
                Population child = childPopulations.get(c);
                if (pi.getRepresentation().distanceTo(child.getBest().getRepresentation())<childDiameter)
                {
                    toBeAdded = false;
                    break;
                }
            }
            if (toBeAdded)
                newParentPopulation.add(pi);
        }

        parentPopulation = newParentPopulation;
    }

    private void adjustPopulation(int c) {

        Population p= childPopulations.get(c);
        Individual best = p.getBest();
        Population newChild = new ListPopulation();

        for (int i=0;i<p.size();i++)
        {
            if ( (!p.get(i).getRepresentation().equals(best.getRepresentation())) &&
                    (p.get(i).getRepresentation().distanceTo(best.getRepresentation())<childDiameter))
                newChild.add(p.get(i));
        }
        newChild.add(best.clone());

        childPopulations.set(c,newChild);

        if (newChild.isEmpty())
            return;

        best = newChild.getBest();

        int diff = minChildPopulation-newChild.size();

        for (int i=0;i<diff;i++)
        {
            childPopulations.get(c).add(best.clone());
        }
    }

    private int getPopulationCount() {
        int total =0;
        for (int i=0;i<childPopulations.size();i++)
            total+=childPopulations.get(i).size();

        total+= parentPopulation.size();
        return total;

    }

    private void removeTooCloseChildren() {
        List<Population> newChildPopulations = new ArrayList<>();
        for (int i=0;i<childPopulations.size();i++)
        {
            Population child = childPopulations.get(i);
            boolean toBeAdded = true;

            for (int j=0; j<childPopulations.size();j++)
            {
                if (i==j)
                    continue;

                Population otherChild = childPopulations.get(j);
                if (child.getBest().getRepresentation().distanceTo(otherChild.getBest().getRepresentation())<childDiameter)
                {
                    if (child.getBestCost()>otherChild.getBestCost())
                    {
                        toBeAdded = false;
                        break;
                    }
                }

            }
            if (toBeAdded)
                newChildPopulations.add(childPopulations.get(i));
        }
        childPopulations = newChildPopulations;
    }


    private void removeEmptyChildren() {
        List<Population> newChildPopulations = new ArrayList<>();
        for (int i=0;i<childPopulations.size();i++)
        {
            if  ( childPopulations.get(i).isEmpty())
                continue;

            newChildPopulations.add(childPopulations.get(i));
        }
        childPopulations = newChildPopulations;
    }

    private void forkIfPossible(OptimizationProblem problem)
    {
        Population childPopulation = createChildPopulation(problem);
        if (childPopulation!= null)
            childPopulations.add(childPopulation);
    }

    private Population createChildPopulation(OptimizationProblem problem) {
        Individual best =parentPopulation.getBest();
        Population childPopulation = null;
        int childCount =0;

        for (int i=0;i<parentPopulation.size();i++)
        {
            if (    (!parentPopulation.get(i).getRepresentation().equals(best.getRepresentation())) &&
                    (parentPopulation.get(i).getRepresentation().distanceTo(best.getRepresentation())<childDiameter) )
                childCount++;
        }

        if (childCount > minChildPopulation)
        {
            childPopulation = new ListPopulation();
            Population newParentPopulation = new ListPopulation();

            for (Individual i:parentPopulation.getIndividuals())
            {
                if (    (i.getRepresentation().equals(best.getRepresentation())) ||
                        (i.getRepresentation().distanceTo(best.getRepresentation())<childDiameter)  )
                {
                    if (childPopulation.size()<maxChildPopulation)
                        childPopulation.add(i);
                }
                else {
                    newParentPopulation.add(i);
                }
            }
            parentPopulation = newParentPopulation;

        }
        return childPopulation;
    }

    private void performECIterations(OptimizationProblem problem) {
        for (int i=0;i<childPopulations.size();i++)
        {
            childPopulations.set(i, generateNextGeneration(problem, childPopulations.get(i))) ;
        }

        parentPopulation = generateNextGeneration(problem,parentPopulation);

    }

    public Population generateNextGeneration(OptimizationProblem problem, Population oldGen) {
        applyCrossOver(problem, oldGen);
        mutationStrategy.applyMutations(problem, oldGen,mutationOperators);
        increaseNeighboringCount(mutationStrategy.getMutationCount());
        updateBestIfNecessary(oldGen.getBest().getRepresentation(), oldGen.getBestCost());

        return oldGen;
    }

    private void applyCrossOver(OptimizationProblem problem, Population oldGen) {
        if (!crossOverOperators.isEmpty())
        {
            List<Individual> parents = parentSelector.selectParents(problem, oldGen.getIndividuals());
            List<Individual> offsprings = crossOverStrategy.generateOffsprings(problem, parents, crossOverOperators);
            List<Individual> victims = victimSelector.selectVictims(problem, oldGen.getIndividuals(), offsprings.size());


            oldGen.removeAll(victims);
            oldGen.add(offsprings);

            increaseNeighboringCount(offsprings.size());
        }
    }



    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<15)
            throw new InvalidParameters("SOS needs 15 params. You provided:"+ params.length);

        List<MutationOperator> moList = EAService.MutationOperators.createMutationOperators(params[0],problem);
        MutationStrategy mutationStrategy = EAService.MutationStrategies.createMutationStrategy(params[1],problem);

        List<CrossOverOperator> coList = EAService.CrossOverOperators.createCrossOverOperators(params[2],problem);
        CrossOverStrategy crossOverStrategy = EAService.CrossOverStrategies.createCrossOverStrategy(params[3],problem);

        ParentSelector ps = EAService.ParentSelectors.createParentSelector(params[4],problem);
        VictimSelector vs = EAService.VictimSelectors.createVictimSelector(params[5],problem);

        TerminalCondition tc = EAService.TerminalConditions.createTerminalCondition(params[6],problem);

        int initialPopulationSize = Integer.parseInt(params[7]);
        int forkInterval = Integer.parseInt(params[8]);
        double childDiameter = Double.parseDouble(params[9]);
        int minChildPopulation = Integer.parseInt(params[10]);
        int maxChildPopulation = Integer.parseInt(params[11]);
        int maxChildren = Integer.parseInt(params[12]);
        int minParentPopulation = Integer.parseInt(params[13]);
        int totalPopulationCount = Integer.parseInt(params[14]);

        SOS sos = new SOS(coList,moList,ps,tc);
        sos.victimSelector = vs;
        sos.mutationStrategy = mutationStrategy;
        sos.crossOverStrategy = crossOverStrategy;
        sos.initialPopulationSize = initialPopulationSize;
        sos.forkInterval= forkInterval;
        sos.childDiameter = childDiameter;
        sos.minChildPopulation = minChildPopulation;
        sos.maxChildPopulation = maxChildPopulation;
        sos.maxChildren = maxChildren;
        sos.minParentPopulation = minParentPopulation;
        sos.totalPopulationCount = totalPopulationCount;

        return sos;
    }
}
