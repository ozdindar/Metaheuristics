package problems.bbob.salomon;

import base.OptimizationProblem;
import problems.bbob.BBOBProblem;
import representation.DoubleVector;
import representation.base.Representation;

public class Salomon implements OptimizationProblem, BBOBProblem {
    private final int dimension;

    public Salomon(int dimension) {
        this.dimension = dimension;
    }


    @Override
    public boolean isFeasible(Representation i) {
        return true;
    }

    @Override
    public double cost(Representation i) {
        double c= 0.0;
        double[] x = ((DoubleVector)i).getValues();

        for (int d = 0; d < dimension; d++) {
            c += x[d]*Math.sin(Math.sqrt(Math.abs(x[d])));
        }
        return -c;
    }

    @Override
    public double upperBound(int d) {
        return 100;
    }

    @Override
    public double lowerBound(int d) {
        return -100;
    }

    @Override
    public double maxDistance() {
        return Math.sqrt(dimension * 10000);
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    public static void main(String[] args) {
        Salomon sch= new Salomon(3);

        double[] x = { 420.968746, 420.968746, 420.968746};
        System.out.println(sch.cost(new DoubleVector(x))/sch.getDimension());

    }
}
