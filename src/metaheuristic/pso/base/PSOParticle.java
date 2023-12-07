package metaheuristic.pso.base;

import representation.base.Representation;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public interface PSOParticle {
    public double getCost();
    public Representation getPosition();
    public Velocity getVelocity();
    public Representation getBestKnownPosition();
    public double getBestKnownCost();


    void setCost(double cost);
    void setBestKnownCost(double cost);

    void setBestKnownPosition(Representation representation);

    void setVelocity(Velocity v);
}
