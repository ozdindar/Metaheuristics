package metaheuristic.pso;

import metaheuristic.pso.base.PSOParticle;
import metaheuristic.pso.base.Velocity;
import representation.base.Representation;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public class SimplePSOParticle implements PSOParticle {

    double cost;
    Representation position;
    Representation bestKownPosition;
    Velocity velocity;
    double bestKnownCost;

    public SimplePSOParticle(double cost, Representation position, Representation bestKnownPosition, double bestKnownCost, Velocity velocity) {
        this.cost = cost;
        this.position = position;
        this.bestKownPosition = bestKnownPosition;
        this.velocity = velocity;
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
    public Velocity getVelocity() {
        return velocity;
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
    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public void setBestKnownCost(double cost) {
        this.bestKnownCost = cost;
    }

    @Override
    public void setBestKnownPosition(Representation representation) {
        bestKownPosition = representation;
    }

    @Override
    public void setVelocity(Velocity v) {
        velocity = v;
    }
}
