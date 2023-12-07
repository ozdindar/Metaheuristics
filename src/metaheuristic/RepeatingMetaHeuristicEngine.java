package metaheuristic;


import base.OptimizationProblem;
import metaheuristic.aco.ACO;
import problems.base.InitialSolutionGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dindar.oz on 19.06.2015.
 */
public class RepeatingMetaHeuristicEngine implements Runnable {

    private final OptimizationProblem problem;
    private final InitialSolutionGenerator solutionGenerator;
    private final MetaHeuristic metaHeuristic;

    private final String prefix;
    private final String outputFileName;
    private boolean debugTrace= false;
    private String suffix= "";

    int repeatCount =10; // Default 10

    public RepeatingMetaHeuristicEngine(MetaHeuristic metaheuristic, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, String outputFileName, String prefix, String suffix, int repeatCount, boolean debugTrace) {
        this(metaheuristic,problem,solutionGenerator,outputFileName,prefix,suffix,debugTrace);
        this.repeatCount = repeatCount;
    }

    public RepeatingMetaHeuristicEngine(MetaHeuristic metaheuristic, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, String outputFileName, String prefix, String suffix, int repeatCount) {
        this(metaheuristic,problem,solutionGenerator,outputFileName,prefix,suffix,false);
        this.repeatCount = repeatCount;
    }

    public RepeatingMetaHeuristicEngine(MetaHeuristic metaheuristic, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, String outputFileName, String prefix, boolean debugTrace) {
        this.metaHeuristic = metaheuristic;
        this.problem = problem;
        this.outputFileName = outputFileName;
        this.prefix= prefix;
        this.solutionGenerator = solutionGenerator;
        this.debugTrace = debugTrace;
    }

    public RepeatingMetaHeuristicEngine(MetaHeuristic metaheuristic, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator,String outputFileName, String prefix, String suffix,boolean debugTrace) {
        this.metaHeuristic = metaheuristic;
        this.problem = problem;
        this.solutionGenerator= solutionGenerator;
        this.outputFileName = outputFileName;
        this.prefix= prefix;
        this.suffix= suffix;
        this.repeatCount = repeatCount;
        this.debugTrace = debugTrace;
    }


    private synchronized void saveResult(String result)
    {
        if (outputFileName ==null || result == null || result.isEmpty())
            return;

        try {
            File outputFile = new File(outputFileName);
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile,true));
            outputWriter.write(result);
            outputWriter.close();

        } catch (IOException ex) {
            Logger.getLogger(RepeatingMetaHeuristicEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run()
    {
        long bestAchieveTime =0;
        long tStart = System.currentTimeMillis();
        double totalCost =0;
        metaHeuristic.setDebugTrace(debugTrace);
        for (int i=0;i<repeatCount;i++) {
            metaHeuristic.init(problem);
            long tdStart = System.currentTimeMillis();
            metaHeuristic.perform(problem,solutionGenerator);
            totalCost += metaHeuristic.getBestKnownCost();
            bestAchieveTime += (metaHeuristic.getBestAchieveTime()-tdStart);
        }
        long tDelta = System.currentTimeMillis() - tStart;
        double elapsedTime = tDelta / (repeatCount*1000.0);
        totalCost /=repeatCount;
        double bestAchieveTimeD = (double)bestAchieveTime/(repeatCount*1000.0);

        String resultStR = ""+ totalCost;
        if (resultStR != null) {
            resultStR = prefix + " " + bestAchieveTimeD + " " + elapsedTime + " " + resultStR + " " + suffix + "\n";
            saveResult(resultStR);
            System.out.print(resultStR);
        }
    }
}
