package metaheuristic.mbo;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oz on 08.08.2015.
 */
public class Flock {
    private static final int MAX_AGE = Integer.MAX_VALUE;
    List<Bird> birds;

    public Flock() {
        this.birds = new ArrayList<>();
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
}
