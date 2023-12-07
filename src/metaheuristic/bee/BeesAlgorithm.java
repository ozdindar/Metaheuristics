package metaheuristic.bee;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.ea.EAIterationEvent;
import metaheuristic.ea.base.MutationOperator;
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
 * Created by dindar.oz on 01.06.2015.
 *
 * Based on "The Bees Algorithm - A Novel Tool for Complex Optimisations Problems"
 *
 */
public class BeesAlgorithm extends AbstractMetaheuristic
{
    List<MutationOperator> neighboringFunctions;
    TerminalCondition terminalCondition;

    int n; // Number Of Scout Bees
    int m; // Number Of selected sites out of n
    int e; // Number Of best sites out of m
    int nep; // Number Of bees reqruited for best e sites
    int nsp;  // number of bees reqruited for other selected sites (m-e)
    int ngh; // initial size of patches


    Population bees = null;

    int iterationCount =0;

    public BeesAlgorithm(List<MutationOperator> neighboringFunctions,TerminalCondition terminalCondition, int n, int m, int e, int nep, int nsp, int ngh) {
        this.neighboringFunctions = neighboringFunctions;
        this.terminalCondition = terminalCondition;
        this.n = n;
        this.m = m;
        this.e = e;
        this.nep = nep;
        this.nsp = nsp;
        this.ngh = ngh;
    }

    @Override
    public String generateResultString() {
        //todo:
        return null;
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
            updateBestIfNecessary(i.getRepresentation(),i.getCost());
            increaseNeighboringCount();
        }
        return initialPopulation;
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        bees = generateInitialPopulation(problem,solutionGenerator,n);


        while (!terminalCondition.isSatisfied(this,bees,problem))
        {
            Population nextGen = generateNextGeneration(problem,bees);

            bees = nextGen;
            iterationCount++;

            Individual best = bees.getBest();
            updateBestIfNecessary(best.getRepresentation(),best.getCost());
            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), best.getCost(),best.getRepresentation()));
            //System.out.println(iterationCount+"-iteration: Average-F:"+ PopulationUtil.averageFitness(population.getIndividuals())+"  Best-F:"+ population.getBest());
        }
        printBest();
    }

    private Population generateNextGeneration(OptimizationProblem problem, Population bees) {

        List<Population> patches = generatePatches(problem,bees);

        Population patchRepresentatives = collectRepresentatives(patches);

        int diff = n- patchRepresentatives.size();
        Population newComers = generateInitialPopulation(problem, solutionGenerator, diff);

        patchRepresentatives.add(newComers);

        return patchRepresentatives;
    }

    private Population collectRepresentatives(List<Population> patches) {
        Population representatives = new ListPopulation();

        for (int i=0;i<patches.size();i++)
        {
            representatives.add(patches.get(i).getBest());
        }
        return representatives;
    }

    private List<Population> generatePatches(OptimizationProblem problem, Population bees) {

        List<Population> patches = new ArrayList<>();
        Population p = selectBest(problem,bees,m);

        patches.addAll(createElitePatches(problem, p));
        patches.addAll(createOtherPatches(problem, p));

        return patches;
    }

    private List<Population> createElitePatches(OptimizationProblem problem, Population p) {
        List<Population> patches = new ArrayList<>();
        for (int i=0;i<e && i<p.size() ;i++ )
        {
            patches.add(createPatch(problem,p.get(i),nep));
        }
        return patches;
    }

    private Population createPatch(OptimizationProblem problem, Individual individual, int nep) {
        Population patch = new ListPopulation();

        patch.add(individual);

        for (int i=1;i<nep;i++)
        {
            patch.add(createNeighbor(problem,individual));
        }
        return patch;
    }

    private Individual createNeighbor(OptimizationProblem problem, Individual individual) {
        Individual neighbor = individual.clone();

        for (int i=0; i<ngh ; i++)
        {
            int nf = RandUtil.randInt(neighboringFunctions.size());
            Representation r = neighboringFunctions.get(nf).apply(problem,neighbor.getRepresentation());
            neighbor = new SimpleIndividual(r, problem.cost(r));
            increaseNeighboringCount();
        }

        return neighbor;
    }

    private List<Population> createOtherPatches(OptimizationProblem problem, Population p) {
        List<Population> patches = new ArrayList<>();
        for (int i=e;i<m ;i++ )
        {
            patches.add(createPatch(problem, p.get(i),nep));
        }
        return patches;
    }

    private Population selectBest(OptimizationProblem problem, Population bees, int m) {
        Population bestBees = new ListPopulation();
        for (int i=0;i<m;i++)
        {
            Individual bestBee= bees.getBest();
            bees.remove(bestBee);
            bestBees.add(bestBee);
        }
        return bestBees;
    }

    @Override
    public String defaultName() {
        return "BA";
    }



}
