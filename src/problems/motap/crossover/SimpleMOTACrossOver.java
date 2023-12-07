package problems.motap.crossover;

import base.OptimizationProblem;
import metaheuristic.ea.base.CrossOverOperator;
import representation.IntegerAssignment;
import representation.base.Representation;

import java.util.Arrays;
import java.util.List;

/**
 * Created by oz on 16.07.2015.
 */
public class SimpleMOTACrossOver implements CrossOverOperator
{
    @Override
    public List<Representation> apply(OptimizationProblem problem, Representation p1, Representation p2) {
      if (p1.equals(p2))
          return Arrays.asList(p1.clone(),p2.clone());
        // removed for performance reasons
        /*  if (!(problem instanceof MOTAProblem))
            throw new InvalidProblem("Works only for MOTA Problems");

        if (!(p1 instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");

        if (!(p2 instanceof IntegerAssignment))
            throw new InvalidIndividual("Works only for Integer Assignments");
*/
        IntegerAssignment offspring1 = (IntegerAssignment) p1.clone();
        IntegerAssignment offspring2 = (IntegerAssignment) p2.clone();

        for (int i =offspring1.getValues().length/2;i<offspring1.getValues().length;i++)
        {
            offspring1.getValues()[i] =  ((IntegerAssignment) p2).getValues()[i];
            offspring2.getValues()[i] =  ((IntegerAssignment) p1).getValues()[i];
        }

        List<Representation> offsprings = Arrays.asList(offspring1,offspring2);
        return offsprings;
    }
}
