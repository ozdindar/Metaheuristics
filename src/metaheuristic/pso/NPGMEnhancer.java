package metaheuristic.pso;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.ea.terminalcondition.IterationBasedTC;
import metaheuristic.pso.base.PSOParticle;
import metaheuristic.pso.base.ContinousProblem;
import metaheuristic.pso.base.ParticleHandler;
import metaheuristic.pso.base.Velocity;
import metrics.partitioning.DoubleVectorPartitioner;
import metrics.partitioning.SearchSpacePartitioner;
import problems.base.InitialSolutionGenerator;
import problems.bbob.BBOBRandomISG;
import problems.bbob.f6_attractive_sector.AttractiveSector;
import representation.DoubleVector;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.DoubleStream;

public class NPGMEnhancer implements ExplorationEnhancer{



    public enum DiscardSelection { Best, Worst, Random};

    private final double epsilon= 0.0001;
    Random rng = new SecureRandom();
    final int discardCount;
    final DiscardSelection discardSelection;


    public NPGMEnhancer(int discardCount, DiscardSelection discardSelection) {
        this.discardCount = discardCount;
        this.discardSelection = discardSelection;
    }

    @Override
    public void apply(ContinousProblem problem, int iteration , List<PSOParticle> swarm, ParticleHandler particleHandler) {
        DoubleVector center = calculateCenter(problem,iteration,swarm);

        discard(problem,swarm);

        List<PSOParticle> newSwarm= generateNewSwarm(problem,center, discardCount, iteration,particleHandler);

        swarm.addAll(newSwarm);
    }

    private void discard(ContinousProblem problem, List<PSOParticle> swarm) {
        switch (discardSelection)
        {
            case Best: discardBest(swarm);break;
            case Worst:discardWorst(swarm);break;
            case Random:discardRandom(swarm);break;
        }
    }

    private void discardRandom(List<PSOParticle> swarm) {
        for (int i = 0; i < discardCount; i++) {
            int x = rng.nextInt(swarm.size());
            swarm.remove(x);
        }
    }

    private void discardWorst(List<PSOParticle> swarm) {
        swarm.sort(Comparator.comparingDouble(PSOParticle::getCost).reversed());
        for (int i = 0; i < discardCount; i++) {
            swarm.remove(swarm.size()-1);
        }
    }

    private void discardBest(List<PSOParticle> swarm) {
        swarm.sort(Comparator.comparingDouble(PSOParticle::getCost));
        for (int i = 0; i < discardCount; i++) {
            swarm.remove(swarm.size()-1);
        }
    }

    private List<PSOParticle> generateNewSwarm(ContinousProblem problem, DoubleVector center, int size, int iteration, ParticleHandler particleHandler) {
        List<PSOParticle> newSwarm= new ArrayList<>();

        for (int i=0;i<size;i++)
        {
            double[] xn= generateNewPosition(problem,center, iteration);
            DoubleVector newRep = new DoubleVector(xn);
            double xnCost = problem.cost(newRep);
            Velocity v = particleHandler.generateInitialVelocity(problem,problem.getDimensionCount());
            PSOParticle particle = new SimplePSOParticle(xnCost,newRep,newRep,xnCost,v);
            newSwarm.add(particle);
        }
        return newSwarm;
    }

    private double[] generateNewPosition(ContinousProblem problem, DoubleVector center, int iteration) {
        double[] xc = center.getValues();
        double[] xn = new double[xc.length];
        for (int d = 0; d < xn.length; d++) {
            xn[d]= xc[d] + problem.getUpperBound(d)*rng.nextDouble()/iteration;
            // TODO: Here is not clear to be checked.
            xn[d]= Math.min(problem.getUpperBound(d),xn[d]);
        }
        return xn;
    }

    private DoubleVector calculateCenter(ContinousProblem problem, int iteration, List<PSOParticle> swarm) {

        double[] xc = new double[problem.getDimensionCount()];

        double minF = swarm.stream().mapToDouble(PSOParticle::getCost).min().getAsDouble();
        double[] fitnessCoeeffs = calculateFitnessCoeffs(swarm,xc.length,minF);
        double sumOfFitnessCoeffs = DoubleStream.of(fitnessCoeeffs).sum();
        for (int d = 0; d < problem.getDimensionCount(); d++) {
            for (int i = 0; i < swarm.size(); i++) {
                double[] x = ((DoubleVector)(swarm.get(i).getPosition())).getValues();
                xc[d] += fitnessCoeeffs[i]*x[d]/sumOfFitnessCoeffs;
            }
        }

        return new DoubleVector(xc);
    }

    private double[] calculateFitnessCoeffs(List<PSOParticle> swarm, int dimension, double minF) {
        double[] fitnessCoeffs = new double[swarm.size()];

        for (int i = 0; i < swarm.size(); i++) {
            fitnessCoeffs[i] = fitnessCoeff(swarm.get(i),minF);
        }
        return fitnessCoeffs;
    }

    private double fitnessCoeff(PSOParticle psoParticle, double minF) {
        double f = psoParticle.getCost();
        f= minF>0 ?  f: f+Math.abs(minF)+ epsilon;
        if (f==0) f+=epsilon;
        return 1.0/f;
    }

    static SearchSpacePartitioner bbobPartitioner(int dimension)
    {
        int slotCount= 10;
        double[] lBounds = new double[dimension];
        double[] uBounds = new double[dimension];
        Arrays.fill(lBounds,-5.0);
        Arrays.fill(uBounds,5.0);
        SearchSpacePartitioner ssp = new DoubleVectorPartitioner(uBounds,lBounds,slotCount);
        return ssp;
    }

    public static void main(String[] args) {
        int dimension = 8;
        SearchSpacePartitioner ssp = bbobPartitioner(dimension);
        InitialSolutionGenerator isg = new BBOBRandomISG();
        TerminalCondition tc = new IterationBasedTC(100000);
        OptimizationProblem problem = new AttractiveSector(dimension,new Random(42));


        PSO pso_npgm = new PSO(new DoubleVectorParticleHandler()
                ,0.5034, 1.0519, 2.3403, // W, C1, C2
                50, // Swarm Size
                tc,
                10); // Stall Threshold);

        pso_npgm.setExplorationEnhancer(new NPGMEnhancer(42, NPGMEnhancer.DiscardSelection.Random));

        pso_npgm.perform(problem,isg);
    }


}
