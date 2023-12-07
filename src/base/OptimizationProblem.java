package base;

import representation.base.Representation;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public interface OptimizationProblem {

    //List<Representation> generateInitialStates(int c);
    //List<Representation> generateInitialStates( RNG rng, int c);
    boolean isFeasible(Representation i);
    double cost(Representation i) ;

    double maxDistance();


}
