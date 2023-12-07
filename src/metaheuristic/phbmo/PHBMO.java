package metaheuristic.phbmo;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAIterationEvent;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.ParentSelector;
import metaheuristic.ea.parentselector.RouletteWheelParentSelector;
import metaheuristic.ss.SSService;
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
 * Created by dindar.oz on 01.06.2015.
 *
 *
 * Based On: "Honey-Bees Mating Optimization(PHBMO) Algorithm: A new heuristic Approach for Water Resources Optimization"
 *
 */
public class PHBMO extends AbstractMetaheuristic
{
    AbstractSMetaheuristic[] workers;

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

    ForkJoinPool pool;



    public PHBMO(AbstractSMetaheuristic worker, TerminalCondition terminalCondition, CrossOverOperator breeding, int dr, int spmax, int bmax, double darsad,int poolSize) {

        this.terminalCondition = terminalCondition;
        this.breeding = breeding;
        this.dr = dr;
        this.spmax = spmax;
        this.spmin = bmax; // For the time being spmin = bmax
        this.bmax = bmax;
        this.darsad = darsad;
        pool = new ForkJoinPool(poolSize);
        workers = new AbstractSMetaheuristic[bmax];

        for (int i = 0; i < bmax; i++) {
            workers[i] = worker.clone();
        }
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

            Population broods= performParallelWork(problem,queen);


            queen = new Queen(broods.getBest());
            updateDrones(broods);


            iterationCount++;
            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), bestKnownCost,bestKnownSolution));

        }
        printBest();

    }

    private Population performParallelWork(OptimizationProblem problem, Queen queen) {
        Population broods = new ListPopulation();
        Collection<Callable<Object>> callables= new ArrayList<>();


        for (int s=0; s<bmax;s++)
        {
            AbstractSMetaheuristic worker = workers[s];

            Individual sperm =  parentSelector.selectParents(problem, queen.getSpermethica()).get(0);
            Representation broodR = breeding.apply(problem,queen.getIndividual().getRepresentation(),sperm.getRepresentation()).get(0);
            Individual brood = new SimpleIndividual(broodR,problem.cost(broodR));
            increaseNeighboringCount();
            Callable<Object> task = new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    improveBrood(problem,brood,worker);
                    broods.add(brood);
                    return null;
                }
            };
            callables.add(task);



        }
        pool.invokeAll(callables);/*todo: stopped here..*/

        return broods;
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


    private void improveBrood(OptimizationProblem problem,Individual brood,AbstractSMetaheuristic worker) {
        for (int i=0 ;i<improveCount;i++)
        {
            worker.setCurrentSolution(brood);

            worker.perform(problem,solutionGenerator);
            Individual neighbor = worker.getCurrentSolution();

            if (neighbor.getCost()<brood.getCost())
                brood.update(neighbor.getRepresentation(),neighbor.getCost());


            increaseNeighboringCount();

        }
        updateBestIfNecessary(brood.getRepresentation(),brood.getCost());


    }






    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<8)
            throw new InvalidParameters("PHBMO needs 8 params. You provided:"+ params.length);


        int poolSize = Integer.parseInt(params[0]);

        AbstractSMetaheuristic localSearch = SSService.SMetaheuristics.createAbstractSMetaheuristic(params[1],problem);

        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[2],problem);
        CrossOverOperator crossOverOperator = EAService.CrossOverOperators.createCrossOverOperator(params[3], problem);
        int dr = Integer.parseInt(params[4]);
        int spmax = Integer.parseInt(params[5]);
        int bmax = Integer.parseInt(params[6]);

        //int bmax = poolSize;
        double darsad = Double.parseDouble(params[7]);

        PHBMO ea = new PHBMO(localSearch,terminalCondition,crossOverOperator,dr,spmax,bmax,darsad,poolSize);

        return ea;
    }


    public Individual getBestSolution() {
        return new SimpleIndividual(bestKnownSolution,bestKnownCost);
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        drones = null;
    }


}
