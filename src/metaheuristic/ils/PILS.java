package metaheuristic.ils;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ss.SSService;
import problems.base.InitialSolutionGenerator;
import representation.base.Individual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

public class PILS extends AbstractSMetaheuristic {



    private int iterationCount=0;


    Perturbator perturbator;

    int poolSize;

    AbstractSMetaheuristic lsClones[];
    ForkJoinPool pool;

    public PILS(AbstractSMetaheuristic localSearch, Perturbator perturbator, TerminalCondition terminalCondition,int poolSize) {
        this.poolSize = poolSize;
        pool = new ForkJoinPool(poolSize);
        lsClones = new AbstractSMetaheuristic[poolSize];


        for (int i = 0; i <poolSize ; i++) {
            lsClones[i] = localSearch.clone();
            lsClones[i].addTerminalCondition(terminalCondition);
        }
        this.terminalCondition = terminalCondition;
        this.perturbator= perturbator;
    }

    public PILS(PILS other) {
        super(other);
        poolSize= other.poolSize;
        lsClones = new AbstractSMetaheuristic[other.lsClones.length];
        for (int i = 0; i <lsClones.length ; i++) {
            lsClones[i] = other.lsClones[i].clone();

        }
        iterationCount = other.iterationCount;
        perturbator = other.perturbator.clone();
    }


    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        while (!terminalCondition.isSatisfied(this,problem))
        {
            Collection<Callable<Object>> callables= new ArrayList<>();

            for (int s=0; s<poolSize;s++)
            {
                AbstractSMetaheuristic ls = lsClones[s];
                Callable<Object> task = new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        ls.perform(problem,solutionGenerator);
                        updateBestIfNecessary(ls.getBestKnownSolution(),ls.getBestKnownCost());
                        Individual lsCurrentSolution = ls.getCurrentSolution();
                        perturbator.perturbate(problem,lsCurrentSolution);
                        ls.setCurrentSolution(lsCurrentSolution);
                        return null;
                    }
                };
                callables.add(task);
            }

            iterationCount++;
            //System.out.println(iterationCount);
            increaseNeighboringCount((int) poolSize);


            pool.invokeAll(callables);



        }
    }

    @Override
    public AbstractSMetaheuristic clone() {
        AbstractSMetaheuristic clone = new PILS(this);

        return clone;
    }


    @Override
    public String defaultName() {
        return "PILS";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        iterationCount=0;
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<4)
            throw new InvalidParameters("PILS needs 4 params. You provided:"+ params.length);


        int poolSize = Integer.parseInt(params[0]);

        AbstractSMetaheuristic localSearch = SSService.SMetaheuristics.createAbstractSMetaheuristic(params[1],problem);

        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[2],problem);

        Perturbator perturbator = EAService.Perturbators.createPerturbator(params[3],problem);

        MetaHeuristic metaHeuristic = new PILS(localSearch,perturbator,terminalCondition,poolSize);

        return metaHeuristic;
    }



}
