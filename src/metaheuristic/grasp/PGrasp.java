package metaheuristic.grasp;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ss.SSService;
import problems.base.InitialSolutionGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;


public class PGrasp extends AbstractSMetaheuristic {



    private int iterationCount=0;

    int poolSize;

    AbstractSMetaheuristic lsClones[];
    ForkJoinPool pool;

    public PGrasp(AbstractSMetaheuristic localSearch, TerminalCondition terminalCondition, int poolSize) {

        this.poolSize = poolSize;
        pool = new ForkJoinPool(poolSize);
        this.terminalCondition = terminalCondition;

        lsClones = new AbstractSMetaheuristic[poolSize];


        for (int i = 0; i <poolSize ; i++) {
            lsClones[i] = localSearch.clone();
            lsClones[i].addTerminalCondition(terminalCondition);
        }
    }

    public PGrasp(PGrasp other) {
        super(other);
        lsClones = new AbstractSMetaheuristic[other.lsClones.length];
        for (int i = 0; i < lsClones.length; i++) {
            lsClones[i]= other.lsClones[i].clone();
        }
        iterationCount = other.iterationCount;
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
                        ls.setCurrentSolution(null);
                        ls.perform(problem,solutionGenerator);
                        updateBestIfNecessary(ls.getBestKnownSolution(),ls.getBestKnownCost());
                        return null;
                    }
                };
                callables.add(task);
            }

            iterationCount++;
            //System.out.println(iterationCount);
            increaseNeighboringCount((int) poolSize);


            pool.invokeAll(callables);
            //fireIterationEvent(new TabuIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
        }

    }

    @Override
    public AbstractSMetaheuristic clone() {
        AbstractSMetaheuristic clone = new PGrasp(this);

        return clone;
    }


    @Override
    public String defaultName() {
        return "PGRASP";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        iterationCount=0;
        for (int i = 0; i < lsClones.length; i++) {
            lsClones[i].init(problem);
        }
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<3)
            throw new InvalidParameters("PGRASP needs 2 params. You provided:"+ params.length);


        int poolSize = Integer.parseInt(params[0]);

        AbstractSMetaheuristic localSearch = SSService.SMetaheuristics.createAbstractSMetaheuristic(params[1],problem);

        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[2],problem);

        MetaHeuristic metaHeuristic = new PGrasp(localSearch,terminalCondition,poolSize);

        return metaHeuristic;
    }





}
