package metaheuristic.gls;

import base.OptimizationProblem;
import representation.base.Representation;

/**
 * Created by dindar.oz on 21.07.2016.
 */
public interface GLSProblem extends OptimizationProblem{
    int getFeatureCount();

    void setFeaturePenalty(int i, int penalty);
    int getFeaturePenalty(int i);

    double getFeatureCost(int i);

    boolean hasFeature(Representation r, int i);
}
