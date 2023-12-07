package metaheuristic.mbo;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import problems.base.InitialSolutionGenerator;
import representation.CostBasedComparator;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;
import util.random.RNG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by oz on 08.08.2015.
 */
public class PFlock {
    private static final int MAX_AGE = Integer.MAX_VALUE;
    List<Bird> birds;
    ForkJoinPool pool;

    public PFlock(int poolSize) {
        this.birds = new ArrayList<>();
        pool = new ForkJoinPool(poolSize);
    }


    public void addBird(Bird bird) {
        birds.add(bird);
    }


    public int getLeaderIndex()
    {
        return birds.size()/2;
    }

    public int getLeftLimit()
    {
        return getLeaderIndex()-1;
    }
    public int getRightLimit()
    {
        return getLeaderIndex()+1;
    }

    public Bird getLeftLimitBird()
    {
        return birds.get(getLeftLimit());
    }

    public Bird getRightLimitBird()
    {
        return birds.get(getRightLimit());
    }

    public Bird getLeader() {
        if (birds.isEmpty())
            return null;
        return birds.get(getLeaderIndex());
    }

    public void setLeader(Bird leader) {
        birds.set(getLeaderIndex(),leader);
    }

    public Bird getBird(int i) {
        if ( birds.size()<=i)
            return null;
        return birds.get(i);
    }

    public void setBird(int i, Bird bird) {
        birds.set(i,bird);
    }

    public void swapBirds(int x, int y)
    {
        Bird temp = birds.get(x);
        birds.set(x, birds.get(y));
        birds.set(y, temp);
    }


    public void swapBestAsLeader()
    {
        int bestIndex= getBestIndex();
        if (bestIndex != getLeaderIndex())
        {
            swapBirds(bestIndex,getLeaderIndex());
        }
    }

    private int getBestIndex() {
        int bestIndex =0;
        Bird best = birds.get(0);

        for (int i=0;i<birds.size();i++)
        {
            if (birds.get(i).getIndividual().getCost()<best.getIndividual().getCost())
            {
                best= birds.get(i);
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    public void age(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        for (Bird b:birds)
        {
            b.age();
            if (b.getAge()>MAX_AGE) {
                Representation r = solutionGenerator.generate(problem,1).get(0);
                Individual i = new SimpleIndividual(r,problem.cost(r));
                b = new Bird(i);
            }
        }
    }


    
    void generateNeighbors(RNG rng, OptimizationProblem problem, MutationOperator neighboringFunction, int nNeighbors, int nShared)
    {
        Collection<Callable<Object> > callables= new ArrayList<>();
        Bird leader= getLeader();
        leader.clearNeighbours();
        Individual iLeader= leader.getIndividual();
        Callable<Object> leaderThread = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                leader.setNeighbours(new Individual[nNeighbors]);
                for (int n=0;n<nNeighbors;n++)
                {
                    Individual neighbor = iLeader;


                    Representation r = neighboringFunction.apply(problem,neighbor.getRepresentation());
                    neighbor = new SimpleIndividual(r, problem.cost(r));
                    leader.getNeighbours()[n] = neighbor;

                }
                Arrays.sort(leader.getNeighbours(),new CostBasedComparator());

                return null;
            }
        };
        //pool.execute(leaderThread);
        callables.add(leaderThread);

        for (int i = 0; i < birds.size(); i++) {
            if (i == getLeaderIndex())
                continue;

            Bird b = getBird(i);
            b.clearNeighbours();
            Individual iBird = b.getIndividual();
            Callable<Object> birdThread = new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    b.setNeighbours(new Individual[nNeighbors-nShared]);
                    for (int n=0;n<nNeighbors-nShared;n++)
                    {
                        Representation r = neighboringFunction.apply(problem,iBird.getRepresentation());
                        Individual neighbor = new SimpleIndividual(r, problem.cost(r));
                        b.getNeighbours()[n]=neighbor;

                    }
                    Arrays.sort(b.getNeighbours(),new CostBasedComparator());

                    return null;

                }
            };

            callables.add(birdThread);

        }
        
        pool.invokeAll(callables);
        /*try {

            pool.shutdown();
            pool.awaitTermination(10000, TimeUnit.SECONDS);




        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
