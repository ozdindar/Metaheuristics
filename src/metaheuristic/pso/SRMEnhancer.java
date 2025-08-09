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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SRMEnhancer implements ExplorationEnhancer{


    private final double theta;

    public enum CenterCalculation { Random, BestOfCurrent, RandomOfCurrent };
    public enum DiscardSelection { Best, Worst, Random};

    Random rng = new SecureRandom();


    final CenterCalculation centerCalculation;
    final DiscardSelection discardSelection;
    final int discardCount;

    public SRMEnhancer(CenterCalculation centerCalculation, DiscardSelection discardSelection, int discardCount) {
        this.centerCalculation = centerCalculation;
        this.discardSelection = discardSelection;
        this.discardCount = discardCount;

        theta = Math.PI*2/discardCount;
    }

    @Override
    public void apply(ContinousProblem problem, int iteration, List<PSOParticle> swarm, ParticleHandler particleHandler) {

        discard(problem,swarm);

        double[] center = calculateCenter(problem,swarm);

        double[] distance = distanceVector(problem);
        for (int k = 0; k < discardCount; k++) {
            double[] xn = new double[distance.length];
            for (int d = 0; d < distance.length; d++) {
                xn[d] = center[d] * Math.cos(theta);
            }
            DoubleVector dv = new DoubleVector(xn);
            double cost = problem.cost(dv);
            Velocity v = particleHandler.generateInitialVelocity(problem,problem.getDimensionCount());
            PSOParticle particle = new SimplePSOParticle(cost,dv,dv,cost,v);
            swarm.add(particle);
        }
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

    private double[] distanceVector(ContinousProblem problem) {
        double[] distance = new double[problem.getDimensionCount()];
        for (int d = 0; d < distance.length; d++) {
            distance[d] = (problem.getUpperBound(d)-problem.getLowerBound(d))*rng.nextDouble();
        }
        return distance;
    }

    private double[] calculateCenter(ContinousProblem problem, List<PSOParticle> swarm) {
        double[] xc= new double[problem.getDimensionCount()];

        switch (centerCalculation){
            case Random:{
                for (int d = 0; d < problem.getDimensionCount(); d++) {
                    double range = problem.getUpperBound(d)-problem.getLowerBound(d);
                    xc[d] = problem.getLowerBound(d) + rng.nextDouble()* range;
                }
                break;
            }
            case BestOfCurrent:{
                DoubleVector best = (DoubleVector) swarm.stream().min(Comparator.comparingDouble(PSOParticle::getCost)).get().getPosition();
                xc = Arrays.copyOf(best.getValues(),best.getLength());
                break;
            }
            case RandomOfCurrent:{
                DoubleVector dv = (DoubleVector) swarm.get(rng.nextInt(swarm.size()));
                xc = Arrays.copyOf(dv.getValues(),dv.getLength());
                break;
            }
        }
        return xc;
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


        PSO pso_srm = new PSO(new DoubleVectorParticleHandler()
                ,0.5034, 1.0519, 2.3403, // W, C1, C2
                50, // Swarm Size
                tc,
                10); // Stall Threshold);

        pso_srm.setExplorationEnhancer(new SRMEnhancer(CenterCalculation.BestOfCurrent,DiscardSelection.Worst,28));

        pso_srm.perform(problem,isg);
    }

}
