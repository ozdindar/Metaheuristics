package metaheuristic.island;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAIterationEvent;
import metaheuristic.ea.EAService;
import problems.base.InitialSolutionGenerator;
import representation.base.Individual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class GenericIsland extends AbstractMetaheuristic
{
    List<IslandModul> modules;
    int moduleCount;
    private int migrationPeriod;
    private ForkJoinPool pool;
    private int immigrantCount;

    private int poolSize;

    public GenericIsland(TerminalCondition terminalCondition,List<IslandModul> modules, int moduleCount, int migrationPeriod, int immigrantCount, int poolSize) {
        this.modules = modules;
        this.moduleCount = moduleCount;
        this.migrationPeriod = migrationPeriod;
        this.immigrantCount = immigrantCount;
        this.terminalCondition = terminalCondition;
        this.poolSize = poolSize;
    }

    @Override
    public String defaultName() {
        return "Island-"+modules;
    }

    int iterationCount;
    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        pool = new ForkJoinPool(poolSize);
        initModules(problem);
        iterationCount=0;
    }

    private void initModules(OptimizationProblem problem) {
        for (int i = 0; i < moduleCount; i++) {
            modules.get(i).init(problem);
        }
    }



    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        while (!terminalCondition.isSatisfied(this,problem))
        {
            runIslands(problem,solutionGenerator);


            Individual best = getGlobalBest();
            updateBestIfNecessary(best.getRepresentation(),best.getCost());
            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), best.getCost(),best.getRepresentation()));
            iterationCount++;
        }

        printBest();
    }

    private void updateNeighboringCount() {
        neighboringCount =0;
        for(IslandModul modul:modules)
        {
            neighboringCount += modul.getNeighboringCount();
        }
    }

    private void migrateIslands() {
        for(int i = 0; i < moduleCount; i++)
        {
            IslandModul to = modules.get(i);

            IslandModul from = modules.get((i+1)%moduleCount);

            ArrayList<Individual> immigrants = from.getImmigrants(immigrantCount);
            to.acceptImmigrants(immigrants);
        }
    }

    private Individual getGlobalBest() {
        Individual best = null;
        for(IslandModul modul:modules)
        {
            if(best == null) best = modul.getBestSolution();
            else if(modul.getBestSolution().getCost()  < best.getCost()) best = modul.getBestSolution();
        }
        return best;
    }

    private void runIslands(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Collection<Callable<Object>> callables= new ArrayList<>();

        for(int i = 0; i < moduleCount; i++)
        {
            final int current = i;
            IslandModul modul = modules.get(i);
            Callable<Object> islandThread = new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    modul.runFor(migrationPeriod, problem, solutionGenerator);
                    return "DONE";
                }
            };
            callables.add(islandThread);
        }

        pool.invokeAll(callables);
        updateNeighboringCount();
        migrateIslands();
    }


    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params, List<IslandModul> moduls) {
        if (params.length<5)
            throw new InvalidParameters("GenericIsland needs 5 params. You provided:"+ params.length);


        TerminalCondition tc = EAService.TerminalConditions.createTerminalCondition(params[0],problem);
        int moduleCount = Integer.parseInt(params[1]);
        int migrationPeriod = Integer.parseInt(params[2]);
        int immigrantCount = Integer.parseInt(params[3]);
        int poolSize = Integer.parseInt(params[4]);

        MetaHeuristic alg = new GenericIsland(tc,moduls,moduleCount,migrationPeriod,immigrantCount,poolSize);

        return alg;
    }

}
