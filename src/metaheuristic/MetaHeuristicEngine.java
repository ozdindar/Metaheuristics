package metaheuristic;


import base.OptimizationProblem;
import metaheuristic.aco.ACO;
import problems.base.InitialSolutionGenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dindar.oz on 19.06.2015.
 */
public class MetaHeuristicEngine implements Runnable {

    private final BufferedWriter outputWriter;
    private final OptimizationProblem problem;
    private final InitialSolutionGenerator solutionGenerator;
    private final MetaHeuristic metaHeuristic;
    private final String prefix;
    private String suffix= "";


    public MetaHeuristicEngine(MetaHeuristic metaheuristic, OptimizationProblem problem,InitialSolutionGenerator solutionGenerator, String prefix) {
        this(metaheuristic,problem,solutionGenerator,null,prefix);

    }

    public MetaHeuristicEngine(MetaHeuristic metaheuristic, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, BufferedWriter outputWriter, String prefix) {
        this.metaHeuristic = metaheuristic;
        this.problem = problem;
        this.outputWriter = outputWriter;
        this.prefix= prefix;
        this.solutionGenerator = solutionGenerator;
    }

    public MetaHeuristicEngine(MetaHeuristic metaheuristic, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, BufferedWriter outputWriter, String prefix,String suffix) {
        this.metaHeuristic = metaheuristic;
        this.problem = problem;
        this.outputWriter = outputWriter;
        this.solutionGenerator = solutionGenerator;
        this.prefix= prefix;
        this.suffix= suffix;
    }


    private synchronized void saveResult(String result)
    {
        if (outputWriter ==null || result == null || result.isEmpty())
            return;

        try {
            outputWriter.write(result);
            outputWriter.flush();
        } catch (IOException ex) {
            Logger.getLogger(ACO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run()
    {
        long tStart = System.currentTimeMillis();
        metaHeuristic.perform(problem,solutionGenerator);
        long tDelta = System.currentTimeMillis() - tStart;
        double elapsedTime = tDelta / 1000.0;
        double bestElapsedtime = (metaHeuristic.getBestAchieveTime()-tStart)/1000.0;

        String resultStR = metaHeuristic.generateResultString();
        if (resultStR != null)
            saveResult(prefix+" "+bestElapsedtime+ " " +elapsedTime+" "+ resultStR+ " "+suffix+"\n") ;
    }
}
