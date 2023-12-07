package metaheuristic.pso;

import base.DynamicOptimizationProblem;
import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import exceptions.WrongIndividualType;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.pso.base.PSOParticle;
import metaheuristic.pso.base.PSOProblem;
import metaheuristic.pso.base.ParticleHandler;
import metaheuristic.pso.base.Velocity;
import problems.base.InitialSolutionGenerator;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public class PSO extends AbstractMetaheuristic {

    double w;
    double siP;
    double siG;

    int iterationCount =0;

    public List<PSOParticle> swarm;
    int swarmSize;

    double minCost = 0;

    int reHopeCount =0;

    ParticleHandler particleHandler;
    private long maxNeighboring;
    private TerminalCondition terminalCondition;

    public PSO(ParticleHandler particleHandler, double w, double siP, double siG, int swarmSize, TerminalCondition tc) {
        this.w = w;
        this.siP = siP;
        this.siG = siG;
        this.particleHandler = particleHandler;
        this.swarmSize = swarmSize;
        this.terminalCondition = tc;
    }


    @Override
    public int getIterationCount() {
        return 0;
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        reHopeCount =0;
        PSOProblem psoProblem = (PSOProblem) problem;
        List<Representation> initialPositions = solutionGenerator.generate(problem,swarmSize);
        swarm = generateInitialParticles(psoProblem, initialPositions);

        while (!terminalCondition.isSatisfied(this,problem)){
            for (int i=0;i<swarm.size();i++)
            {
                PSOParticle psoParticle = swarm.get(i);
                updateParticle(psoProblem, psoParticle,  w, siG, siP);

                if (problem instanceof DynamicOptimizationProblem)
                    bestKnownCost = problem.cost(bestKnownSolution);
                updateBestIfNecessary(psoParticle.getPosition().clone(),psoParticle.getCost());
            }
            if (noHopeCase())
            {
                reHopeCount++;
                reHope(psoProblem);
                //System.out.println("NOHOPE");
            }
            iterationCount++;

            fireIterationEvent(new PSOIterationEvent(iterationCount,getNeighboringCount(),bestKnownCost,bestKnownSolution));
            //System.out.println("REHOPE: "+reHopeCount+"  Iteration Count: " + iterationCount + " Cost: " + bestKnownCost);
        }

        printBest();

    }

    @Override
    public String defaultName() {
        return "PSO";
    }

    private void reHope(PSOProblem problem) {
        /*if (reHopeCount>0 && reHopeCount%5==0)
        {
            bestKnownPosition = null;
            bestKnownCost= Double.MAX_VALUE;
        }*/
        List<Representation> newPositions = solutionGenerator.generate(problem,swarmSize);
        swarm= generateInitialParticles(problem,newPositions);
    }

    private boolean noHopeCase() {
        int diversity = 0;
        for (int i=0;i<swarm.size();i++)
        {
            PSOParticle p = swarm.get(i);

            if (!p.getVelocity().isNullVelocity()) {
                diversity++;
                continue;
            }
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

//    private boolean isFinished() {
//        return ( ((maxNeighboring>0)&&(neighboringCount>maxNeighboring)) || ((minCost>=0)&&(bestKnownCost<=minCost)));
//    }

    private List<PSOParticle> generateInitialParticles(PSOProblem problem, List<Representation> initialPositions)
    {
        List<PSOParticle> pList = new ArrayList<PSOParticle>();
        for (int i=0; i<initialPositions.size();i++ )
        {
            PSOParticle particle = generateInitialParticle(problem, initialPositions.get(i));

            updateBestIfNecessary(particle.getPosition(),particle.getCost());

            increaseNeighboringCount();
            pList.add(particle);
        }
        return pList;
    }

    public void updateParticle(PSOProblem psoProblem, PSOParticle psoParticle,double w,double siG,double siP) {

        updateVelocity(psoParticle,w,siG,siP);

        Representation position = psoParticle.getPosition();
        Velocity velocity = psoParticle.getVelocity();

        velocity.move(position);
        increaseNeighboringCount(); // We got new neighboring

        psoParticle.setCost(psoProblem.cost(position));
        if (psoParticle.getCost()<psoParticle.getBestKnownCost())
        {
            psoParticle.setBestKnownPosition(psoParticle.getPosition().clone());
            psoParticle.setBestKnownCost(psoParticle.getCost());

        }
    }

    public void updateVelocity(PSOParticle p,  double w, double siG, double siP) {
        Velocity velocity = p.getVelocity();
        Representation position = p.getPosition();
        Representation best = p.getBestKnownPosition();

        double rP = RandUtil.randDouble();
        double rG = RandUtil.randDouble();

        Velocity selfConfidence = velocity.multiply(w);
        Velocity memory = velocity.distance(best, position).multiply(siP*rP);
        Velocity faith = velocity.distance(bestKnownSolution, position).multiply(siG*rG);



        p.setVelocity(selfConfidence.add(memory).add(faith));

    }

    public PSOParticle generateInitialParticle(PSOProblem problem, Representation initialPosition) {

        Class c = particleHandler.getRepresentationClass();
        if (!( c.isInstance(initialPosition )))
            throw new WrongIndividualType("Wrong ParticleHandler");

        double cost = problem.cost(initialPosition);

        return new SimplePSOParticle(cost,initialPosition,initialPosition.clone(),cost,particleHandler.generateInitialVelocity(problem, problem.getDimensionCount()));
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {

        if (params.length<6)
            throw new InvalidParameters("PSO needs 6 params. You provided:"+ params.length);

        ParticleHandler particleHandler = PSOService.ParticleHandlers.createParticleHandler(params[0],problem);
        double w = Double.parseDouble(params[1]);
        double siP = Double.parseDouble(params[2]);
        double siG = Double.parseDouble(params[3]);
        int swarmSize = Integer.parseInt(params[4]);
        TerminalCondition tc = EAService.TerminalConditions.createTerminalCondition(params[5],problem);


        return new PSO(particleHandler,w,siP,siG,swarmSize, tc);
    }
}
