package metaheuristic.tabu;

import base.NeighboringFunction;
import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.tabu.base.Aspiration;
import metaheuristic.tabu.base.TabuList;
import problems.base.InitialSolutionGenerator;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.List;

/**
 * Created by dindar.oz on 01.06.2015.
 */
public class TabuSearch extends AbstractSMetaheuristic {

    long globalIterationCount= 0;

    List<NeighboringFunction> neighborhoodFunctions;

    TabuList tabuList = null;
    Aspiration aspiration= null;

    public void setClearMemoryAtInit(boolean clearMemoryAtInit) {
        this.clearMemoryAtInit = clearMemoryAtInit;
    }

    boolean clearMemoryAtInit = false;

    public void setIntensityMemory(MediumTermMemory intensityMemory) {
        this.intensityMemory = intensityMemory;
    }

    MediumTermMemory intensityMemory = null;

    public void setDiversityMemory(MediumTermMemory diversityMemory) {
        this.diversityMemory = diversityMemory;
    }

    MediumTermMemory diversityMemory = null;

    public void setNeighborhoodSize(int neighborhoodSize) {
        this.neighborhoodSize = neighborhoodSize;
    }

    int neighborhoodSize =0;

    int iterationCount =0;

    private int intensificationInterval=400;
    private int diversificationInterval=2000;

    public TabuSearch(TerminalCondition terminalCondition, List<NeighboringFunction> neighborhoodFunctions, TabuList tabuList, Aspiration aspiration, int neighborhoodSize) {
        this.terminalCondition = terminalCondition;
        this.neighborhoodFunctions = neighborhoodFunctions;
        this.tabuList = tabuList;
        this.aspiration = aspiration;
        this.neighborhoodSize = neighborhoodSize;
    }

    public String generateResultString() {
        return bestKnownCost+"";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }


    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        initTabuSearch(problem,solutionGenerator);


        while (!isTerminated(problem))
        {
            Individual ns =  selectBestNeighborhood(problem,currentSolution,neighborhoodFunctions);


            updateCurrentSolution(problem,ns);


            iterationCount++;
            globalIterationCount++;
            fireIterationEvent(new TabuIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));

            if (intensificationRequired())
            {
                intensify(problem);
            }

            // FOR PERFORMANCE. TO BE REMOVED LATER
            if (diversificationRequired())
            {
                diversify(problem);
            }

        }

        //printBest();
    }

    @Override
    public AbstractSMetaheuristic clone() {
        throw new RuntimeException("Not implemented");
    }

    private boolean intensificationRequired() {
        return intensificationInterval>0 && globalIterationCount>0 && globalIterationCount%intensificationInterval==0;
    }
    private boolean diversificationRequired() {
        return diversificationInterval>0 && globalIterationCount>0 && globalIterationCount%diversificationInterval==0;
    }

    private void intensify(OptimizationProblem problem) {
        if (intensityMemory != null) {
            Representation representation = intensityMemory.generate(problem);
            updateCurrentSolution(problem,new SimpleIndividual(representation,problem.cost(representation)));
        }
    }

    private void diversify(OptimizationProblem problem) {
        if (diversityMemory != null) {
            Representation representation = diversityMemory.generate(problem);
            updateCurrentSolution(problem,new SimpleIndividual(representation,problem.cost(representation)));
        }
    }

    private void initTabuSearch(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
         if( intensityMemory != null)
            intensityMemory.init(problem,clearMemoryAtInit);
        if(diversityMemory != null)
            diversityMemory.init(problem,clearMemoryAtInit);

        iterationCount =0;
    }

    @Override
    public String defaultName() {
        return "TABU";
    }



    private void updateCurrentSolution(OptimizationProblem problem, Individual ns)
    {
        updateBestIfNecessary(ns.getRepresentation(),ns.getCost());

        if (intensityMemory != null)
            intensityMemory.update(problem,ns,null);
        currentSolution = ns;

        tabuList.record(problem,currentSolution.getRepresentation());

        // FOR PERFORMANCE. TO BE REMOVED LATER
        if (diversityMemory!=null)
            diversityMemory.update(problem,ns,null);

        increaseNeighboringCount();
    }

    private boolean isTerminated(OptimizationProblem problem) {
        return (terminalCondition!= null &&  terminalCondition.isSatisfied(this,problem));
    }


    private Individual selectBestNeighborhood(OptimizationProblem problem, Individual currentSolution, List<NeighboringFunction> neighborhoodFunctions)
    {
        Representation best= null;
        double bestCost =Double.MAX_VALUE;


        for (int i=0 ;i<neighborhoodSize;i++)
        {
            Individual tmp= null;
            NeighboringFunction nf = null;

            nf = neighborhoodFunctions.get(RandUtil.randInt(neighborhoodFunctions.size()))  ;
            tmp =nf.apply(problem,currentSolution);


            increaseNeighboringCount();
            if ( !isTabu(problem,tmp.getRepresentation(),nf)|| isAspired(tmp.getRepresentation(),tmp.getCost(),nf) || best == null)
            {
                if (best==null || tmp.getCost()<bestCost )
                {
                    best = tmp.getRepresentation();
                    bestCost = tmp.getCost();
                }
            }

        }

        return new SimpleIndividual(best,bestCost);
    }

    private boolean isAspired(Representation tmp,double cost, NeighboringFunction nf) {
        if (cost<bestKnownCost)
            return  true;

        if (aspiration == null)
            return false;

        return aspiration.isAspired(tmp,cost,nf);
    }

    private boolean isTabu(OptimizationProblem problem, Representation tmp, NeighboringFunction nf) {
        return (tabuList.isTabu(problem,tmp,nf));
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<7)
            throw new InvalidParameters("TABU Search needs 7 params. You provided:"+ params.length);

        List<NeighboringFunction> neighborhoodFunctions = TabuService.NeighboringFunctions.createNeighboringFunctions(params[0],problem);
        TabuList tabuList = TabuService.TabuLists.createTabuList(params[1],problem);
        Aspiration aspiration = TabuService.Aspirations.createAspiration(params[2],problem);
        MediumTermMemory intensityMemory = TabuService.MediumTermMemories.createMediumTermMemory(params[3],problem);
        MediumTermMemory diversityMemory = TabuService.MediumTermMemories.createMediumTermMemory(params[4],problem);
        int neighborhoodSize = Integer.parseInt(params[5]);
        TerminalCondition tc = EAService.TerminalConditions.createTerminalCondition(params[6],problem);

        TabuSearch ts = new TabuSearch(tc,neighborhoodFunctions,tabuList,aspiration,neighborhoodSize);
        ts.setIntensityMemory(intensityMemory);
        ts.setDiversityMemory(diversityMemory);
        return ts;
    }
}
