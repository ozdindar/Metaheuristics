package problems.bbob;

import metaheuristic.pso.base.ContinousProblem;

public interface BBOBProblem extends ContinousProblem {
    // dimension count
    int getDimension();

    // upper bound in each dimension
    default double upperBound(int d){return 5;};

    // lower bound in each dimension
    default double lowerBound(int d){return -5;};

    // volue of the function at position x
    default double evaluate(double[] x) {return 0;};

    @Override
    default int getDimensionCount() {
        return getDimension();
    }

    @Override
    default double getUpperBound(int d){
        return upperBound(d);
    }

    @Override
    default double getLowerBound(int d){
        return lowerBound(d);
    }
}
