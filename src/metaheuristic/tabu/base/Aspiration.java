package metaheuristic.tabu.base;

import base.NeighboringFunction;
import representation.base.Representation;

/**
 * Created by dindar.oz on 23.06.2015.
 */
public interface Aspiration {

    boolean isAspired(Representation tmp, double cost, NeighboringFunction nf);

}
