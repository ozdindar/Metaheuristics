package metaheuristic.pso.base;

import representation.base.Representation;

/**
 * Created by dindar.oz on 17.06.2015.
 */
public interface Velocity {

    public Velocity multiply(double c);
    public Velocity distance(Representation r1, Representation r2);

    public void move(Representation r);

    public Velocity add(Velocity v);

    public boolean isNullVelocity();
}

