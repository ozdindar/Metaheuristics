package metaheuristic.sso;


import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.island.IslandModul;
import metaheuristic.tabu.TabuIterationEvent;
import problems.base.InitialSolutionGenerator;
import representation.CostBasedComparator;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 16.12.2016.
 */
public class SSO extends AbstractMetaheuristic implements IslandModul {

    List<SSOParticle> ssoParticles;


    double Cg = 0.5, Cp= 0.85 ,Cw= 0.95;


    private int iterationCount=0;
    private int particleCount=50;


    public SSO(TerminalCondition terminalCondition,double cg, double cp, double cw, int particleCount) {
        Cg = cg;
        Cp = cp;
        Cw = cw;
        this.particleCount = particleCount;
        this.terminalCondition = terminalCondition;
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }

    @Override
    protected synchronized void increaseNeighboringCount() {
        neighboringCount++;
    }

    public List<SSOParticle> generateInitialParticles(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, int c) {
        List<Representation> initialStates = solutionGenerator.generate(problem,c);
        List<SSOParticle> initialParticles = new ArrayList<>();
        for(Representation r: initialStates)
        {
            Individual i = new SimpleIndividual(r,problem.cost(r));
            SSOParticle ssoParticle = new SSOParticle(i);
            initialParticles.add(ssoParticle);
            updateBestIfNecessary(i.getRepresentation(),i.getCost());
            increaseNeighboringCount();
        }
        return initialParticles;
    }

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        ssoParticles = null;
        iterationCount =0;
    }

    @Override
    public void runFor(int migrationPeriod, OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        if (ssoParticles==null)
        {
            ssoParticles = generateInitialParticles(problem,solutionGenerator,particleCount);
        }


        for (int i = 0; i < migrationPeriod; i++) {

            updateParticles(problem);
            iterationCount++;
            //fireIterationEvent(new TabuIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
        }
        int done =1;
    }

    @Override
    public Individual getBestSolution() {
        return new SimpleIndividual(bestKnownSolution,bestKnownCost);
    }

    @Override
    public ArrayList<Individual> getImmigrants(int immigrantCount) {
        List<Individual> solutions = getSolutions(); ;
        solutions.sort(new CostBasedComparator());
        ArrayList<Individual> immigrants  = new ArrayList<>(immigrantCount);
        for (int i = 0; i < immigrantCount; i++) {
            immigrants.add(solutions.get(i));
        }

        return immigrants;
    }

    private List<Individual> getSolutions() {
        List<Individual> solutions = new ArrayList<>();
        for (int i = 0; i < ssoParticles.size(); i++) {
            solutions.add(ssoParticles.get(i).getSolution());
        }
        return solutions;
    }

    @Override
    public void acceptImmigrants(ArrayList<Individual> immigrants) {
        updatePopulation(immigrants);
    }

    private void updatePopulation(ArrayList<Individual> immigrants) {
        for (int i = 0; i < immigrants.size(); i++) {
            int victim = RandUtil.randInt(ssoParticles.size());
            ssoParticles.get(victim).setSolution(immigrants.get(i));
        }

    }


    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        iterationCount =0;

        ssoParticles = generateInitialParticles(problem,solutionGenerator, particleCount);


        while (!terminalCondition.isSatisfied(this,problem))
        {
            updateParticles(problem);
            iterationCount++;
            fireIterationEvent(new TabuIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
        }

        printBest();

    }

    private void updateParticles(OptimizationProblem problem) {
        Representation globalBest = bestKnownSolution.clone();
        for (int i = 0; i < ssoParticles.size(); i++) {
            updateParticle(ssoParticles.get(i),problem,globalBest);
        }
    }

    private void updateParticle(SSOParticle ssoParticle, OptimizationProblem problem, Representation globalBest) {
        ssoParticle.update(problem,globalBest,Cg,Cp,Cw);
        updateBestIfNecessary(ssoParticle.getPersonalBest().getRepresentation(),ssoParticle.getPersonalBest().getCost());
        increaseNeighboringCount();
    }


    @Override
    public String defaultName() {
        return "SSO";
    }

    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<2)
            throw new InvalidParameters("SSO needs 2 params. You provided:"+ params.length);

        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[0],problem);

        int particleCount = Integer.parseInt(params[1]);
        SSO sso = new SSO(terminalCondition ,0.50,0.85,0.95,particleCount);


        return sso;
    }
}
