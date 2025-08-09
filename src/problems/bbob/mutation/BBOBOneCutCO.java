package problems.bbob.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.CrossOverOperator;
import representation.DoubleVector;
import representation.IntegerAssignment;
import representation.base.Representation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BBOBOneCutCO implements CrossOverOperator {
    @Override
    public List<Representation> apply(OptimizationProblem problem, Representation p1, Representation p2) {
        if (p1.equals(p2))
            return Arrays.asList(p1.clone(),p2.clone());

        DoubleVector offspring1 = (DoubleVector) p1.clone();
        DoubleVector offspring2 = (DoubleVector) p2.clone();

        for (int i =offspring1.getValues().length/2;i<offspring1.getValues().length;i++)
        {
            offspring1.getValues()[i] =  ((DoubleVector) p2).getValues()[i];
            offspring2.getValues()[i] =  ((DoubleVector) p1).getValues()[i];
        }

        List<Representation> offsprings = Arrays.asList(offspring1,offspring2);
        return offsprings;
    }
}
