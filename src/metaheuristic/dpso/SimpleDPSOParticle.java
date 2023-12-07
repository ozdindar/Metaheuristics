package metaheuristic.dpso;

import metaheuristic.dpso.base.DPSOParticle;
import representation.base.Representation;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public class SimpleDPSOParticle implements DPSOParticle {

    double cost;
    Representation position;
    Representation bestKownPosition;
    double bestKnownCost;

    public SimpleDPSOParticle(double cost, Representation position, Representation bestKnownPosition, double bestKnownCost) {
        this.cost = cost;
        this.position = position;
        this.bestKownPosition = bestKnownPosition;
        this.bestKnownCost = bestKnownCost;
    }

    @Override
    public double getCost() {
        return cost;
    }

    @Override
    public Representation getPosition() {
        return position;
    }



    @Override
    public Representation getBestKnownPosition() {
        return bestKownPosition;
    }

    @Override
    public double getBestKnownCost() {
        return bestKnownCost ;
    }

    @Override
    public void setPosition(Representation position) {
        this.position = position;
    }

    @Override
    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public void setBestKnownCost(double cost) {
        this.bestKnownCost = cost;
    }

    @Override
    public void update(Representation pos, double cost) {
        setPosition(pos);
        setCost(cost);
        if (cost<bestKnownCost)
        {
            bestKnownCost = cost;
            bestKownPosition = pos.clone();
        }
    }

    @Override
    public void setBestKnownPosition(Representation representation) {
        bestKownPosition = representation;
    }

}
