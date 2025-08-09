package metrics.populationheatmap;

import representation.base.Individual;
import representation.base.Representation;

import java.util.Comparator;
import java.util.List;

public class PHMNode {

    private final String id;
    public long firstVisit;
    public long lastVisit;

    public long firstTouch;
    public long lastTouch;

    Representation bRep;
    double bCost;
    long bCostUpdate;

    private long touchCount;
    private long visitCount;




    public PHMNode(String id) {
        this.id= id;
    }

    public String getID() {
        return id;
    }

    public long getTouchCount() {
        return touchCount;
    }

    public long getVisitCount() {
        return visitCount;
    }

    public Representation getbRep() {
        return bRep;
    }

    public void updateBest(Individual i, long iteration) {
        if (bRep == null|| i.getCost()<bCost)
        {
            bCost = i.getCost();
            bRep = i.getRepresentation().clone();
            bCostUpdate = iteration;
        }
    }

    public void update(List<Individual> individuals, long iteration) {
        if (individuals.isEmpty())
            return;

        touch(iteration);

        updateBest(individuals.stream().max(Comparator.comparingDouble(Individual::getCost).reversed()).get(), iteration);

    }

    private void touch(long iteration) {
        touchCount++;
        if (firstTouch==0)
            firstTouch=iteration;
        lastTouch= iteration;
    }

    public void visit(long iteration) {
        visitCount++;
        if (firstVisit==0)
            firstVisit=iteration;
        lastVisit= iteration;
    }

    @Override
    public String toString() {
        return id;
    }
}
