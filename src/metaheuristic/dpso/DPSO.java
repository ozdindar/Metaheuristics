package metaheuristic.dpso;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.dpso.base.DPSOParticle;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.CrossOverOperator;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.crossover.OneCutCrossover;
import metaheuristic.ea.mutation.InsertMutation;
import problems.base.InitialSolutionGenerator;
import problems.motap.crossover.SimpleMOTACrossOver;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 03.06.2015.
 */

/**
 * this Discrete PSO is written according to
 * See: Pan, tasgetiren, Liang, A Discrete PSO algorithm for the no-wait
 * flowshop scheduling problem, 2008, (journal)
 * @author falkaya
 *
 */
public class DPSO extends AbstractMetaheuristic {

    private static final int MUTATION_COUNT = 1;
    double w;
    double siP;
    double siG;

    int iterationCount =0;

    List<DPSOParticle> swarm;
    int swarmSize;

    //double minCost = 0;

    int reHopeCount =0;

    boolean allowRepetition = false; // Allow repetition in the representation. False for permutations

    private TerminalCondition terminalCondition;
    private MutationOperator mutation = new InsertMutation(MUTATION_COUNT);
    private CrossOverOperator crossOver = new OneCutCrossover();
    private boolean reHopeEnabled = false;

    public DPSO(double w, double siP, double siG, int swarmSize, TerminalCondition terminalCondition) {
        this.w = w;
        this.siP = siP;
        this.siG = siG;
        this.swarmSize = swarmSize;
        this.terminalCondition= terminalCondition;
    }

    public DPSO(double w, double siP, double siG, int swarmSize, TerminalCondition terminalCondition,boolean allowRepetition) {
        this.w = w;
        this.siP = siP;
        this.siG = siG;
        this.swarmSize = swarmSize;
        this.terminalCondition= terminalCondition;
        this.allowRepetition = allowRepetition;

        if (allowRepetition) // Extension for repeating representations
            crossOver = new SimpleMOTACrossOver();
    }

    @Override
    public int getIterationCount() {
        return 0;
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator)
    {
        reHopeCount =0;
        List<Representation> initialPositions = solutionGenerator.generate(problem,swarmSize);
        swarm = generateInitialParticles(problem, initialPositions);

        while (!terminalCondition.isSatisfied(this,problem)){
            for (int i=0;i<swarm.size();i++)
            {
                DPSOParticle DPSOParticle = swarm.get(i);
                updateParticle(problem, DPSOParticle,  w, siG, siP);


            }

            if (noHopeCase())
            {
                reHopeCount++;
                reHope(problem,solutionGenerator);
            }

            iterationCount++;
            fireIterationEvent(new DPSOIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
        }

        printBest();

    }

    @Override
    public String defaultName() {
        return "DPSO";
    }

    private void reHope(OptimizationProblem problem,InitialSolutionGenerator solutionGenerator) {
        /*if (reHopeCount>0 && reHopeCount%5==0)
        {
            bestKnownPosition = null;
            bestKnownCost= Double.MAX_VALUE;
        }*/
        List<Representation> newPositions = solutionGenerator.generate(problem,swarmSize);
        swarm= generateInitialParticles(problem,newPositions);
    }

    private boolean noHopeCase() {

        if (!reHopeEnabled)
            return false;

        int diversity = 0;
        for (int i=0;i<swarm.size();i++)
        {
            DPSOParticle p = swarm.get(i);


            if (!p.getPosition().equals(bestKnownSolution)) {
                diversity++;
                continue;
            }
            if (!p.getPosition().equals(p.getBestKnownPosition()))
            {
                diversity++;
                continue;
            }

        }

        return diversity<swarm.size()/2;
    }


    private List<DPSOParticle> generateInitialParticles(OptimizationProblem problem, List<Representation> initialPositions)
    {
        List<DPSOParticle> pList = new ArrayList<DPSOParticle>();
        for (Representation initialPosition: initialPositions)
        {
            double cost = problem.cost(initialPosition);
            DPSOParticle particle = new SimpleDPSOParticle(cost,initialPosition,initialPosition.clone(),cost);

            updateBestIfNecessary(particle.getPosition(),particle.getCost());

            pList.add(particle);
            increaseNeighboringCount();
        }
        return pList;
    }

    public void updateParticle(OptimizationProblem psoProblem, DPSOParticle DPSOParticle,double w,double siG,double siP) {

        Representation newPosition = DPSOParticle.getPosition().clone();

        if (RandUtil.rollDice(w)) // Mutate by itself
        {
            newPosition = mutation.apply(psoProblem,newPosition);
            increaseNeighboringCount();
        }

        if (RandUtil.rollDice(siP)) //  CrossOver by personal best
        {
            newPosition = crossOver.apply(psoProblem,newPosition, DPSOParticle.getBestKnownPosition()).get(0);
            increaseNeighboringCount();
        }

        if (RandUtil.rollDice(siG)) //  CrossOver by global best
        {
            newPosition = crossOver.apply(psoProblem,newPosition,bestKnownSolution).get(0);
            increaseNeighboringCount();
        }

        DPSOParticle.update(newPosition, psoProblem.cost(newPosition));
        updateBestIfNecessary(DPSOParticle.getPosition().clone(), DPSOParticle.getCost());
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {

        if (params.length<5)
            throw new InvalidParameters("D-PSO needs 5 params. You provided:"+ params.length);

        double w = Double.parseDouble(params[0]);
        double siP = Double.parseDouble(params[1]);
        double siG = Double.parseDouble(params[2]);
        int swarmSize = Integer.parseInt(params[3]);
        TerminalCondition tc = EAService.TerminalConditions.createTerminalCondition(params[4],problem);

        return new DPSO(w,siP,siG,swarmSize,tc);
    }
}
