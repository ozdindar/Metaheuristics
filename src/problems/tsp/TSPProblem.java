package problems.tsp;

import metaheuristic.gls.GLSProblem;
import representation.base.Representation;

/**
 * Created by dindar.oz on 21.07.2016.
 */
public class TSPProblem implements GLSProblem {

    public int getCityCount() {
        return cityCount;
    }

    int cityCount;
    double distanceMatrix[][];

    public TSPProblem(int cityCount, double[][] distanceMatrix) {
        this.cityCount = cityCount;
        this.distanceMatrix = distanceMatrix;

        _initFeatures();
    }

    private void _initFeatures() {

    }

    @Override
    public int getFeatureCount() {
        return 0;
    }

    @Override
    public void setFeaturePenalty(int i, int penalty) {

    }

    @Override
    public int getFeaturePenalty(int i) {
        return 0;
    }

    @Override
    public double getFeatureCost(int i) {
        return 0;
    }

    @Override
    public boolean hasFeature(Representation r, int i) {
        return false;
    }



    @Override
    public boolean isFeasible(Representation i) {
        return false;
    }

    @Override
    public double cost(Representation i) {
        return 0;
    }

    @Override
    public double maxDistance() {
        return 0;
    }
}
