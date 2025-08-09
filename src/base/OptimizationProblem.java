package base;

import representation.base.Representation;

/**
 * Created by dindar.oz on 03.06.2015.
 */
public interface OptimizationProblem {

    // Returnss if the given solution is feasible
    boolean isFeasible(Representation i);

    // returns the objective value of the given solution
    double cost(Representation i) ;

    // reeturns the maximum distance(difference) possible between any two solutions
    double maxDistance();

    // (If applicable) returns the dimension count
    default int getDimension(){ return 0;}


}
