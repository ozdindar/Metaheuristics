package metaheuristic.dpso.base;

import representation.base.Representation;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public interface DPSOParticle {
    public double getCost();
    public Representation getPosition();
    public Representation getBestKnownPosition();
    public double getBestKnownCost();


    void setPosition(Representation position);
    void setCost(double cost);
    void setBestKnownCost(double cost);

    void update(Representation pos, double cost);
    void setBestKnownPosition(Representation representation);

}
