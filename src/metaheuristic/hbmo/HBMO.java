package metaheuristic.hbmo;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAIterationEvent;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.parentselector.RouletteWheelParentSelector;
import metaheuristic.island.IslandModul;
import problems.base.InitialSolutionGenerator;
import problems.motap.mutation.GreedySearch;
import representation.CostBasedComparator;
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
 *
 * Based On: "Honey-Bees Mating Optimization(PHBMO) Algorithm: A new heuristic Approach for Water Resources Optimization"
 *
 */
public class HBMO extends AbstractMetaheuristic implements IslandModul
{
    List<MutationOperator> workers;

    ParentSelector parentSelector = new RouletteWheelParentSelector(1);
    CrossOverOperator breeding;

    private int iterationCount=0;

    int dr; // Number of drones
    int spmax; //spermetheca Size
    int spmin; // min smermethica size
    int bmax; // Max number of broods
    double darsad; // Percentage of broods should be replaced with drones
    int mfmax; // Number of mating flights;

    Population drones;
    private int improveCount=1;


    public HBMO(List<MutationOperator> workers, TerminalCondition terminalCondition, CrossOverOperator breeding, int dr, int spmax, int bmax, double darsad) {
        this.workers = workers;
        this.terminalCondition = terminalCondition;
        this.breeding = breeding;
        this.dr = dr;
        this.spmax = spmax;
        this.spmin = bmax; // For the time being spmin = bmax
        this.bmax = bmax;
        this.darsad = darsad;
    }

    @Override
    public String defaultName() {
        return "PHBMO";
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
    public void perform(OptimizationProblem problem,InitialSolutionGenerator solutionGenerator) {

        iterationCount =0;
        drones = generateInitialPopulation(problem,solutionGenerator,dr);
        Queen queen = new Queen(drones.getBest());

        while (!terminalCondition.isSatisfied(this,problem))
        {
            queen.init(problem,solutionGenerator);

            queen.doMatingFlightOver(drones, spmin, spmax);

            Population broods= new ListPopulation();
            for (int s=0; s<bmax;s++)
            {
                Individual sperm =  parentSelector.selectParents(problem, queen.getSpermethica()).get(0);
                Representation broodR = breeding.apply(problem,queen.getIndividual().getRepresentation(),sperm.getRepresentation()).get(0);
                Individual brood = new SimpleIndividual(broodR,problem.cost(broodR));
                increaseNeighboringCount();
                brood = improveBrood(problem,brood);

                broods.add(brood);
            }
            queen = new Queen(broods.getBest());
            updateDrones(broods);


            iterationCount++;
            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), bestKnownCost,bestKnownSolution));

        }
        printBest();

    }

    private void updateDrones(Population broods) {
        drones.getIndividuals().sort(new CostBasedComparator());


        int toBeRemoved = (int) (drones.size()*darsad);
        for (int i=0 ;i<toBeRemoved;i++)
            drones.remove(drones.size()-1);

        broods.getIndividuals().sort(new CostBasedComparator());

        for (int i=0 ;i<toBeRemoved;i++)
            drones.add(broods.get(i));



    }

    private void updateDrones(ArrayList<Individual> immigrants) {
        drones.getIndividuals().sort(new CostBasedComparator());


        int toBeRemoved = (int) (drones.size()*darsad);
        for (int i=0 ;i<toBeRemoved;i++)
            drones.remove(drones.size()-1);

        for (int i=0 ;i<toBeRemoved;i++)
            drones.add(immigrants.get(i));



    }


    private Individual improveBrood(OptimizationProblem problem,Individual brood) {
        for (int i=0 ;i<improveCount;i++)
        {
            MutationOperator worker = selectWorker();
            Representation r = worker.apply(problem, brood.getRepresentation());
            double rcost = problem.cost(r);
            if (rcost<brood.getCost())
                brood = new SimpleIndividual(r,rcost);

            if (worker instanceof  GreedySearch)
                increaseNeighboringCount(((GreedySearch)worker).improveCount);
            else increaseNeighboringCount();

        }
        updateBestIfNecessary(brood.getRepresentation(),brood.getCost());

        return brood;
    }



    private MutationOperator selectWorker() {
        return workers.get(RandUtil.randInt(workers.size()));
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<7)
            throw new InvalidParameters("PHBMO needs 7 params. You provided:"+ params.length);

        List<MutationOperator> workers = EAService.MutationOperators.createMutationOperators(params[0],problem);
        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[1],problem);
        CrossOverOperator crossOverOperator = EAService.CrossOverOperators.createCrossOverOperator(params[2], problem);
        int dr = Integer.parseInt(params[3]);
        int spmax = Integer.parseInt(params[4]);
        int bmax = Integer.parseInt(params[5]);
        double darsad = Double.parseDouble(params[6]);

        HBMO ea = new HBMO(workers,terminalCondition,crossOverOperator,dr,spmax,bmax,darsad);

        return ea;
    }

    @Override
    public void runFor(int iteration, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        if (drones == null)
            drones = generateInitialPopulation(problem,solutionGenerator,dr);
        Queen queen = new Queen(drones.getBest());

        for (int i = 0; i < iteration; i++) {
            queen.init(problem,solutionGenerator);

            queen.doMatingFlightOver(drones, spmin, spmax);

            Population broods= new ListPopulation();
            for (int s=0; s<bmax;s++)
            {
                Individual sperm =  parentSelector.selectParents(problem, queen.getSpermethica()).get(0);
                Representation broodR = breeding.apply(problem,queen.getIndividual().getRepresentation(),sperm.getRepresentation()).get(0);
                Individual brood = new SimpleIndividual(broodR,problem.cost(broodR));
                increaseNeighboringCount();
                brood = improveBrood(problem,brood);

                broods.add(brood);
            }
            queen = new Queen(broods.getBest());
            updateDrones(broods);


            iterationCount++;
            //fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), bestKnownCost,bestKnownSolution));

        }
        int done =3;
    }

    @Override
    public Individual getBestSolution() {
        return new SimpleIndividual(bestKnownSolution,bestKnownCost);
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        drones = null;
    }

    @Override
    public ArrayList<Individual> getImmigrants(int immigrantCount) {
        drones.sort(new CostBasedComparator());
        ArrayList<Individual> immigrants  = new ArrayList<>(immigrantCount);
        for (int i = 0; i < immigrantCount; i++) {
            immigrants.add(drones.get(i));
        }

        return immigrants;
    }

    @Override
    public void acceptImmigrants(ArrayList<Individual> immigrants) {
        updateDrones(immigrants);
    }
}
