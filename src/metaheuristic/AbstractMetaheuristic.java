package metaheuristic;



import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.aco.ACO;
import metaheuristic.ea.EAIterationEvent;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dindar.oz on 22.06.2015.
 */
public abstract class AbstractMetaheuristic implements MetaHeuristic{

    protected double bestAchieveTime=0;
    protected double bestKnownCost =Double.MAX_VALUE;
    protected Representation bestKnownSolution = null;

    protected long neighboringCount = 0;

    protected String name = null;
    protected TerminalCondition terminalCondition=null;

    public boolean debugTrace = false;
    private long startTime;


    public AbstractMetaheuristic()
    {

    }

    public AbstractMetaheuristic(AbstractMetaheuristic metaheuristic) {
        startTime = metaheuristic.startTime;
        bestAchieveTime = metaheuristic.bestAchieveTime;
        bestKnownCost = metaheuristic.bestKnownCost;
        if (metaheuristic.bestKnownSolution != null)
            bestKnownSolution = metaheuristic.bestKnownSolution.clone();
        manualEventGeneration= metaheuristic.manualEventGeneration;
        neighboringCount = metaheuristic.neighboringCount;
        if (metaheuristic.name!= null)
            name = new String(metaheuristic.name);
        if (metaheuristic.terminalCondition != null)
            terminalCondition = metaheuristic.terminalCondition.clone();
    }

    public void setManualEventGeneration(boolean manualEventGeneration) {
        this.manualEventGeneration = manualEventGeneration;
    }

    private boolean manualEventGeneration= false;

    public void setDebugTrace(boolean debugTrace)
    {
        this.debugTrace= debugTrace;
    }

    public InitialSolutionGenerator getSolutionGenerator() {
        return solutionGenerator;
    }

    public void setSolutionGenerator(InitialSolutionGenerator solutionGenerator) {
        this.solutionGenerator = solutionGenerator;
    }

    protected InitialSolutionGenerator solutionGenerator= null;

    @Override
    public String getName() {
        if (name ==null)
            return defaultName();
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public abstract String defaultName();

    @Override
    public void setName(String name) {
        this.name = name;
    }


    public void init(OptimizationProblem problem)
    {
        neighboringCount=0;
        bestKnownSolution = null;
        bestKnownCost =Double.MAX_VALUE;
        if (terminalCondition != null)
            terminalCondition.init();

        if (debugTrace)
        {    startTime = System.currentTimeMillis();
             logStart();
        }
    }

    private void logStart() {
        String traceFileName = getName()+"_trace.txt";
        String line = "START "+ startTime+"\n";
        try {
            File outputFile = new File(traceFileName);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile,true));
            outputWriter.write(line);
            outputWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(ACO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    protected synchronized void increaseNeighboringCount()
    {
        neighboringCount++;
        fireIterationEvent(new EAIterationEvent(neighboringCount,neighboringCount,bestKnownCost,bestKnownSolution));
    }
    protected synchronized void increaseNeighboringCount(int c)
    {
        neighboringCount+=c;
        fireIterationEvent(new EAIterationEvent(neighboringCount,neighboringCount,bestKnownCost,bestKnownSolution));
    }

    @Override
    public double getBestAchieveTime() {
        return bestAchieveTime;
    }

    @Override
    public long getNeighboringCount() {
        return neighboringCount;
    }

    @Override
    public double getBestKnownCost() {
        return bestKnownCost;
    }

    @Override
    public Representation getBestKnownSolution() {
        return bestKnownSolution;
    }

    protected void updateBestIfNecessary(Representation solution, double cost) {
        if (cost<bestKnownCost)
        {
            bestKnownSolution = solution;
            bestKnownCost = cost;
            bestAchieveTime = System.currentTimeMillis();

            if (debugTrace)
                logBest();
        }
    }

    private void logBest() {
        String traceFileName = getName()+"_trace.txt";
        String line = (System.currentTimeMillis()-startTime)+" " +neighboringCount+ " "+bestKnownCost+"\n";
        try {
            File outputFile = new File(traceFileName);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile,true));
            outputWriter.write(line);
            outputWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(ACO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void logEnd() {
        String traceFileName = getName()+"_trace.txt";
        String line = "END "+ System.currentTimeMillis()+"\n";
        try {
            File outputFile = new File(traceFileName);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile,true));
            outputWriter.write(line);
            outputWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(ACO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    List<MetaHeuristicListener> listeners= new ArrayList<>();

    public void addListener(MetaHeuristicListener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(MetaHeuristicListener listener)
    {
        listeners.remove(listener);
    }

    public void fireIterationEvent(IterationEvent event)
    {
        for( MetaHeuristicListener listener:listeners)
        {
            listener.onIterationEvent(event);
        }
    }


    protected void printBest() {
        System.out.println("["+getName()+"] Best Cost: "+ bestKnownCost+ " ItrCnt:"+ getIterationCount() +" Best: "+ bestKnownSolution);
    }

    @Override
    public String generateResultString() {
        return bestKnownCost+"";
    }

/*    public void solve(OptimizationProblem problem)
    {
        init();

        while (!isTerminated(problem))
        {
            iterate(problem);
        }
        printBest();
    }

    protected abstract void iterate(OptimizationProblem problem);

    protected abstract boolean isTerminated(OptimizationProblem problem);

    protected abstract void init();*/

}
